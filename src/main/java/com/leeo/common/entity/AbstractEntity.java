package com.leeo.common.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.Persistable;

/**
 * 抽象实体基类，如果主键是数据库端自动生成 请使用{@link BaseEntity}，如果是Oracle 请使用{@link BaseOracleEntity}
 */
public abstract class AbstractEntity<ID extends Serializable> implements Persistable<ID> {

	private static final long serialVersionUID = 2365223595660882895L;

    @Override
    public abstract ID getId();

    public abstract void setId(final ID id);
    
    //声明两个时间列，用来作为创建时间和更新时间
    @Temporal(TemporalType.TIMESTAMP)
    private Date update_timestamp;
 
    @Temporal(TemporalType.TIMESTAMP)
    private Date create_timestamp;
 
    //在创建时，对创建时间和更新时间进行刷新
    @PrePersist
    public void prePersist(){
        this.update_timestamp = new Date();
        this.create_timestamp = this.update_timestamp;
    }
 
    //在更新时，对更新时间进行刷新
    @PreUpdate
    public void preUpdate(){
        this.update_timestamp = new Date();
    }
 
    public Date getCreate_timestamp() {
        return this.create_timestamp;
    }
 
    public void setCreate_timestamp(Date create_timestamp) {
        this.create_timestamp = create_timestamp;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.data.domain.Persistable#isNew()

     */
    @Override
    public boolean isNew() {

        return null == getId();
    }

    @Override
    public boolean equals(Object obj) {

        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        AbstractEntity<?> that = (AbstractEntity<?>) obj;

        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

//    @Override
//    public int hashCode() {
//        return HashCodeBuilder.reflectionHashCode(this);
//    }

    @Override
    public String toString() {
//        return ReflectionToStringBuilder.toString(this);
        return ToStringBuilder.reflectionToString(this,
            ToStringStyle.SHORT_PREFIX_STYLE);
    }
}