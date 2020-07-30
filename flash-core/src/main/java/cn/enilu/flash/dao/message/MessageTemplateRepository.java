package cn.enilu.flash.dao.message;


import cn.enilu.flash.bean.entity.message.MessageTemplate;
import cn.enilu.flash.dao.BaseRepository;

import java.util.List;


/**
 * @author cs
 * @date 2020/07/28
 */
public interface MessageTemplateRepository extends BaseRepository<MessageTemplate, Long> {
    /**
     * 根据code查询
     * @param code code
     * @return MessageTemplate
     */
    MessageTemplate findByCode(String code);

    /**
     * 根据消息ID查询
     * @param idMessageSender 消息ID
     * @return List
     */
    List<MessageTemplate> findByIdMessageSender(Long idMessageSender);
}

