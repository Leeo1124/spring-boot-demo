package com.leeo.sys.group.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.leeo.common.repository.BaseRepository;
import com.leeo.sys.group.entity.Group;

@Repository
public interface GroupRepository extends BaseRepository<Group, Long>{
    
    @Query("select id from Group where defaultGroup=true and show=true")
    List<Long> findDefaultGroupIds();
}