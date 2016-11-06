package com.leeo.sys.role.repository;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.leeo.common.repository.BaseRepository;
import com.leeo.sys.role.entity.Role;

@Repository
public interface RoleRepository extends BaseRepository<Role, Long>{
    
    /**
     * 没搞明白@QueryHints这个标签的作用
     * @param name
     * @param pageable
     * @return
     */
    @QueryHints(value = { @QueryHint(name = "name", value = "value")}, forCounting = false)
    Page<Role> findByName(String name, Pageable pageable);
}