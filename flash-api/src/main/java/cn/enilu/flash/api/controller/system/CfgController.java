package cn.enilu.flash.api.controller.system;

import cn.enilu.flash.api.controller.BaseController;
import cn.enilu.flash.bean.constant.factory.PageFactory;
import cn.enilu.flash.bean.core.BussinessLog;
import cn.enilu.flash.bean.entity.system.Cfg;
import cn.enilu.flash.bean.entity.system.FileInfo;
import cn.enilu.flash.bean.enumeration.BizExceptionEnum;
import cn.enilu.flash.bean.enumeration.Permission;
import cn.enilu.flash.bean.exception.ApplicationException;
import cn.enilu.flash.bean.vo.front.Results;
import cn.enilu.flash.bean.vo.query.SearchFilter;
import cn.enilu.flash.service.system.CfgService;
import cn.enilu.flash.service.system.FileService;
import cn.enilu.flash.service.system.LogObjectHolder;
import cn.enilu.flash.utils.Maps;
import cn.enilu.flash.utils.StringUtil;
import cn.enilu.flash.utils.factory.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * CfgController
 *
 * @author enilu
 * @version 2018/11/17 0017
 */
@RestController
@RequestMapping("/cfg")
@Slf4j
public class CfgController extends BaseController {
    @Resource
    private CfgService cfgService;
    @Resource
    private FileService fileService;

    private Page<Cfg> initCfgPage(String cfgName, String cfgValue){
        Page<Cfg>  page = new PageFactory<Cfg>().defaultPage();
        if (StringUtil.isNotEmpty(cfgName)) {
            page.addFilter(SearchFilter.build("cfgName", SearchFilter.Operator.LIKE, cfgName));
        }
        if (StringUtil.isNotEmpty(cfgValue)) {
            page.addFilter(SearchFilter.build("cfgValue", SearchFilter.Operator.LIKE, cfgValue));
        }
        page = cfgService.queryPage(page);
        return page;
    }
    /**
     * 查询参数列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @RequiresPermissions(value = {"/cfg"})
    public Object list(@RequestParam(required = false) String cfgName, @RequestParam(required = false) String cfgValue) {
        Page<Cfg> page = initCfgPage(cfgName,cfgValue);
        return Results.success(page);
    }

    /**
     * 导出参数列表
     *
     * @param cfgName 配置名称
     * @param cfgValue 配置值
     * @return Object
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @RequiresPermissions(value = {Permission.CFG})
    public Object export(@RequestParam(required = false) String cfgName, @RequestParam(required = false) String cfgValue) {
        Page<Cfg> page = initCfgPage(cfgName,cfgValue);
        FileInfo fileInfo = fileService.createExcel("templates/config.xlsx", "系统参数.xlsx", Maps.newHashMap("list", page.getRecords()));
        return Results.success(fileInfo);
    }

    @RequestMapping(method = RequestMethod.POST)
    @BussinessLog(value = "新增参数", key = "cfgName")
    @RequiresPermissions(value = {"/cfg/add"})
    public Object add(@ModelAttribute @Valid Cfg cfg) {
        cfgService.saveOrUpdate(cfg);
        return Results.success();
    }

    @RequestMapping(method = RequestMethod.PUT)
    @BussinessLog(value = "编辑参数", key = "cfgName")
    @RequiresPermissions(value = {"/cfg/update"})
    public Object update(@ModelAttribute @Valid Cfg cfg) {
        Cfg old = cfgService.get(cfg.getId());
        LogObjectHolder.me().set(old);
        old.setCfgName(cfg.getCfgName());
        old.setCfgValue(cfg.getCfgValue());
        old.setCfgDesc(cfg.getCfgDesc());
        cfgService.saveOrUpdate(old);
        return Results.success();
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @BussinessLog(value = "删除参数", key = "id")
    @RequiresPermissions(value = {"/cfg/delete"})
    public Object remove(@RequestParam Long id) {
        log.info("id:{}", id);
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        cfgService.delete(id);
        return Results.success();
    }
}
