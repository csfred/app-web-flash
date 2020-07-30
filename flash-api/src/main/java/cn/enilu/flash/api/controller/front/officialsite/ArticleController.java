package cn.enilu.flash.api.controller.front.officialsite;

import cn.enilu.flash.api.controller.BaseController;
import cn.enilu.flash.bean.entity.cms.Article;
import cn.enilu.flash.bean.vo.front.Results;
import cn.enilu.flash.service.cms.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author cs
 * @date 2020/07/28
 */
@Slf4j
@RestController
@RequestMapping("/offcialsite/article")
public class ArticleController extends BaseController {
    @Resource
    private ArticleService articleService;

    @RequestMapping(method = RequestMethod.GET)
    public Object get(@Param("id") Long id, @Param("type") String type) {
        log.info("type:{},id:{}", type, id);
        Article article = articleService.get(id);
        return Results.success(article);
    }
}
