package cn.enilu.flash.dao.cms;

import cn.enilu.flash.bean.entity.cms.Banner;
import cn.enilu.flash.dao.BaseRepository;

import java.util.List;

/**
 * @author cs
 * @date 2020/07/28
 */
public interface BannerRepository extends BaseRepository<Banner, Long> {
    /**
     * 查询指定类别的banner列表
     *
     * @param type 类别
     * @return List
     */
    List<Banner> findAllByType(String type);
}
