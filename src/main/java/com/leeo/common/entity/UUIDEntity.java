package com.leeo.common.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
public abstract class UUIDEntity<ID extends Serializable> extends AbstractEntity<ID> {
    
    private static final long serialVersionUID = -2884478182724856695L;
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private ID id;
 
    @Override
    public ID getId() {
        return this.id;
    }
 
    @Override
    public void setId(ID id) {
        this.id = id;
    }
}