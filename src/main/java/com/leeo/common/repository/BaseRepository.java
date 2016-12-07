package com.leeo.common.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.leeo.common.entity.search.Searchable;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

//    List<T> findByIdIn(Iterable<ID> ids);
//    
//    @Query("select t from #{#entityName} t where t.id in(?1)")
//    Collection<T> findALL(Iterable<ID> ids);
    
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
    
    /**
     * 根据主键查询实体
     *
     * @param ids
     * @return
     */
    public List<T> findAll(ID[] ids);
    
    /**
     * 
    * @Title: findbySql
    * @Description: 通过sql查询语句查找对象
    * @param @param sql
    * @param @param objs
    * @param @return    参数
    * @return List<T>    返回类型
    * @throws
     */
    public List<T> findbySql(final String sql, final Object... objs);
    
    @SuppressWarnings("hiding")
	public <T> List<T> findListbySql(final String sql, final Object... objs);
    
    public int executeSql(final String sql, final List<Object> values);
}
