package cn.enilu.flash.api.controller.message;

import cn.enilu.flash.bean.constant.factory.PageFactory;
import cn.enilu.flash.bean.core.BussinessLog;
import cn.enilu.flash.bean.entity.message.MessageTemplate;
import cn.enilu.flash.bean.enumeration.BizExceptionEnum;
import cn.enilu.flash.bean.enumeration.Permission;
import cn.enilu.flash.bean.exception.ApplicationException;
import cn.enilu.flash.bean.vo.front.Results;
import cn.enilu.flash.bean.vo.query.SearchFilter;
import cn.enilu.flash.service.message.MessagetemplateService;
import cn.enilu.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author cs
 * @date 2020/07/28
 */
@RestController
@RequestMapping("/message/template")
public class MessageTemplateController {
    @Resource
    private MessagetemplateService messagetemplateService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @RequiresPermissions(value = {Permission.MSG_TPL})
    public Object list(@RequestParam(name = "idMessageSender", required = false) Long idMessageSender,
                       @RequestParam(name = "title", required = false) String title) {
        Page<MessageTemplate> page = new PageFactory<MessageTemplate>().defaultPage();
//        page.addFilter("idMessageSender",idMessageSender);
        //也可以通过下面关联查询的方式
        page.addFilter("messageSender.id", idMessageSender);
        page.addFilter("title", SearchFilter.Operator.LIKE, title);

        page = messagetemplateService.queryPage(page);
        page.setRecords(page.getRecords());
        return Results.success(page);
    }

    @RequestMapping(method = RequestMethod.POST)
    @BussinessLog(value = "编辑消息模板", key = "name")
    @RequiresPermissions(value = {Permission.MSG_TPL_EDIT})
    public Object save(@ModelAttribute @Valid MessageTemplate messageTemplate) {
        if (messageTemplate.getId() == null) {
            messagetemplateService.insert(messageTemplate);
        } else {
            messagetemplateService.update(messageTemplate);
        }
        return Results.success();
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @BussinessLog(value = "删除消息模板", key = "id")
    @RequiresPermissions(value = {Permission.MSG_TPL_DEL})
    public Object remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        messagetemplateService.delete(id);
        return Results.success();
    }
}
