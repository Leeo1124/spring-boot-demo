package com.leeo.sys.user.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.leeo.common.entity.BaseEntity;

/**
 * 为了提高连表性能 使用数据冗余 而不是 组织机构(1)-----(*)职务的中间表
 * 即在该表中 用户--组织机构--职务 是唯一的  但 用户-组织机构可能重复
 */
@Entity
@Table(name = "sys_user_organization")
//@EnableQueryCache
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserOrganization extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
    @Basic(optional = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private User user;

    @Column(name = "organization_id")
    private Long organizationId;



    public UserOrganization() {
    }


    public UserOrganization(Long organizationId) {
        this.organizationId = organizationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }


    @Override
    public String toString() {
        return "UserOrganization{id = " + this.getId() +
                ",organizationId=" + organizationId +
                ", userId=" + (user != null ? user.getId() : "null") +
                '}';
    }
}
