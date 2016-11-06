package com.leeo.sys.resource.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.leeo.common.repository.BaseRepository;
import com.leeo.sys.resource.entity.Resource;

@Repository
public interface ResourceRepository extends BaseRepository<Resource, Long>{
    
	 @Query("select r from Resource r where r.parentId is null order by weight")
	 List<Resource> findMenu();
}