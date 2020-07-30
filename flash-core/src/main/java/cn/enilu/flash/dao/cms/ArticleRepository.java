package cn.enilu.flash.dao.cms;

import cn.enilu.flash.bean.entity.cms.Article;
import cn.enilu.flash.dao.BaseRepository;

import java.util.List;

/**
 * @author cs
 * @date 2020/07/28
 */
public interface ArticleRepository extends BaseRepository<Article, Long> {
    /**
     * 查询指定栏目下所有文章列表
     *
     * @param idChannel ID渠道
     * @return List
     */
    List<Article> findAllByIdChannel(Long idChannel);
}
