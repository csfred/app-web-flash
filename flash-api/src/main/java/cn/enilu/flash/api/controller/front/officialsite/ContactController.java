package cn.enilu.flash.api.controller.front.officialsite;

import cn.enilu.flash.api.controller.BaseController;
import cn.enilu.flash.bean.entity.cms.Contacts;
import cn.enilu.flash.bean.vo.front.Results;
import cn.enilu.flash.service.cms.ContactsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author cs
 * @date 2020/07/28
 */
@Slf4j
@RestController
@RequestMapping("/offcialsite/contact")
public class ContactController extends BaseController {
    @Resource
    private ContactsService contactsService;

    @RequestMapping(method = RequestMethod.POST)
    public Object save(@Valid Contacts contacts) {
        contactsService.insert(contacts);
        return Results.success();
    }
}
