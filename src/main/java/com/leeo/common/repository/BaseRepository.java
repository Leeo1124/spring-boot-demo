package com.leeo.common.repository;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.leeo.common.entity.search.Searchable;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

//    List<M> findByIdIn(Collection<ID> ids);
//    
//    @Query("select t from #{#entityName} t where t.id in(?1)")
//    Collection<M> findALL(Collection<ID> ids);
    
    //---------------------------------------------
    /**
     * 根据主键删除
     *
     * @param ids
     */
    public void delete(ID[] ids);

    /**
     * 根据条件查询所有
     * 条件 + 分页 + 排序
     *
     * @param searchable
     * @return
     */
    public Page<T> findAll(Searchable searchable);


    /**
     * 根据条件统计所有记录数
     *
     * @param searchable
     * @return
     */
    public long count(Searchable searchable);
}
