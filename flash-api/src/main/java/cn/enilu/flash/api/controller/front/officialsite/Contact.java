package cn.enilu.flash.api.controller.front.officialsite;

import lombok.Data;

import java.util.Date;

/**
 * @author cs
 * @date 2020/07/28
 */
@Data
public class Contact {
    public String username;
    private String email;
    private String mobile;
    private String description;
    private Date createAt;
}
