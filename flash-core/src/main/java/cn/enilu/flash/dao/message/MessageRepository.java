package cn.enilu.flash.dao.message;


import cn.enilu.flash.bean.entity.message.Message;
import cn.enilu.flash.dao.BaseRepository;

import java.util.ArrayList;


/**
 * @author cs
 * @date 2020/07/28
 */
public interface MessageRepository extends BaseRepository<Message, Long> {
    /**
     * 根据ID集合删除所有
     * @param list id集合
     */
    void deleteAllByIdIn(ArrayList<String> list);
}

