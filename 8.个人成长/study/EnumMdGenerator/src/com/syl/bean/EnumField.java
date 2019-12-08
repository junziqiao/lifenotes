package com.syl.bean;

/**
 * @author wangzejun
 * @create 2018-06-22 15:30
 **/
public class EnumField {

    /**
     * 枚举名称
     */
    private String enumName;

    /**
     * 内容
     */
    private String enumContent;

    /**
     * 备注
     */
    private String enumComment;

    public EnumField() {
    }

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    public String getEnumContent() {
        return enumContent;
    }

    public void setEnumContent(String enumContent) {
        this.enumContent = enumContent;
    }

    public String getEnumComment() {
        return enumComment;
    }

    public void setEnumComment(String enumComment) {
        this.enumComment = enumComment;
    }
}
