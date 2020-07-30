package cn.enilu.flash.api.controller;

import cn.enilu.flash.api.utils.ApiConstants;
import cn.enilu.flash.bean.core.ShiroUser;
import cn.enilu.flash.bean.entity.system.User;
import cn.enilu.flash.bean.vo.front.Results;
import cn.enilu.flash.core.log.LogManager;
import cn.enilu.flash.core.log.LogTaskFactory;
import cn.enilu.flash.security.ShiroFactroy;
import cn.enilu.flash.service.system.UserService;
import cn.enilu.flash.utils.HttpUtil;
import cn.enilu.flash.utils.MD5;
import cn.enilu.flash.utils.Maps;
import cn.enilu.flash.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.mapl.Mapl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * AccountController
 *
 * @author enilu
 * @version 2018/9/12 0012
 */
@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {

    @Resource
    @Lazy
    private UserService userService;

    /**
     * 用户登录<br>
     * 1，验证没有注册<br>
     * 2，验证密码错误<br>
     * 3，登录成功
     *
     * @param userName 用户名
     * @param password 密码
     * @return Object
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object login(@RequestParam("username") String userName,
                        @RequestParam("password") String password) {
        try {
            //1,
            User user = userService.findByAccount(userName);
            if (user == null) {
                return Results.failure("用户名或密码错误");
            }
            String passwdMd5 = MD5.md5(password, user.getSalt());
            //2,
            if (!user.getPassword().equals(passwdMd5)) {
                return Results.failure("用户名或密码错误");
            }
            if (StringUtil.isEmpty(user.getRoleid())) {
                return Results.failure("该用户未配置权限");
            }
            String token = userService.loginForToken(user);
            Map<String, String> result = new HashMap<>(1);
            result.put("token", token);
            LogManager.me().executeLog(LogTaskFactory.loginLog(user.getId(), HttpUtil.getIp()));
            return Results.success(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Results.failure("登录时失败");
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Object info() {
        HttpServletRequest request = HttpUtil.getRequest();
        Long idUser;
        try {
            idUser = getIdUser(request);
        } catch (Exception e) {
            return Results.expire();
        }
        if (idUser != null) {
            User user = userService.get(idUser);
            if (StringUtil.isEmpty(user.getRoleid())) {
                return Results.failure("该用户未配置权限");
            }
            ShiroUser shiroUser = ShiroFactroy.me().shiroUser(user);
            Map<String,Object> map = Maps.newHashMap("name", user.getName(),
                    "role", "admin", "roles", shiroUser.getRoleCodes());
            map.put("permissions", shiroUser.getUrls());
            Map<String,Object> profile = (Map) Mapl.toMaplist(user);
            profile.put("dept", shiroUser.getDeptName());
            profile.put("roles", shiroUser.getRoleNames());
            map.put("profile", profile);

            return Results.success(map);
        }
        return Results.failure("获取用户信息失败");
    }

    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    public Object updatePwd(@RequestParam("oldPassword") String oldPassword,
                            @RequestParam("password") String password,
                            @RequestParam("rePassword") String rePassword) {
        try {

            if (StringUtil.isEmpty(password) || StringUtil.isEmpty(rePassword)) {
                return Results.failure("密码不能为空");
            }
            if (!password.equals(rePassword)) {
                return Results.failure("新密码前后不一致");
            }
            User user = userService.get(getIdUser(HttpUtil.getRequest()));
            if (ApiConstants.ADMIN_ACCOUNT.equals(user.getAccount())) {
                return Results.failure("不能修改超级管理员密码");
            }
            if (!MD5.md5(oldPassword, user.getSalt()).equals(user.getPassword())) {
                return Results.failure("旧密码输入错误");
            }

            user.setPassword(MD5.md5(password, user.getSalt()));
            userService.update(user);
            return Results.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Results.failure("更改密码失败");
    }

}