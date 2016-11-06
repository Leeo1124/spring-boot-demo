package com.leeo.sys.group.entity;

/**
 * 用户组分类
 */
public enum GroupType {

    user("用户组"), admin("管理员组"), organization("组织机构组");

    private final String info;

    private GroupType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
