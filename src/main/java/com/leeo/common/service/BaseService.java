package com.leeo.common.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.leeo.common.entity.AbstractEntity;
import com.leeo.common.entity.search.Searchable;
import com.leeo.common.repository.BaseRepository;

@Transactional(readOnly = true)
public abstract class BaseService<T extends AbstractEntity<?>, ID extends Serializable> {

	protected BaseRepository<T, ID> baseRepository;

    public abstract void setRepository(BaseRepository<T, ID> baseRepository);
    
    /**
     * 保存单个实体
     *
     * @param t 实体
     * @return 返回保存的实体
     */
    @Transactional
    public T save(T t) {
        return baseRepository.save(t);
    }

    @Transactional
    public T saveAndFlush(T t) {
        t = save(t);
        baseRepository.flush();
        return t;
    }

    /**
     * 更新单个实体
     *
     * @param t 实体
     * @return 返回更新的实体
     */
    @Transactional
    public T update(T t) {
        return baseRepository.saveAndFlush(t);
    }

    /**
     * 根据主键删除相应实体
     *
     * @param id 主键
     */
    @Transactional
    public void delete(ID id) {
        baseRepository.delete(id);
    }

    /**
     * 删除实体
     *
     * @param t 实体
     */
    @Transactional
    public void delete(T t) {
        baseRepository.delete(t);
    }

    /**
     * 根据主键删除相应实体
     *
     * @param ids 实体
     */
    @Transactional
    public void delete(ID[] ids) {
        baseRepository.delete(ids);
    }

    /**
     * 按照主键查询
     *
     * @param id 主键
     * @return 返回id对应的实体
     */
    public T findOne(ID id) {
        return baseRepository.findOne(id);
    }

    /**
     * 实体是否存在
     *
     * @param id 主键
     * @return 存在 返回true，否则false
     */
    public boolean exists(ID id) {
        return baseRepository.exists(id);
    }

    /**
     * 统计实体总数
     *
     * @return 实体总数
     */
    public long count() {
        return baseRepository.count();
    }
    
    /**
     * 查询所有实体
     *
     * @return
     */
    public List<T> findAll() {
        return baseRepository.findAll();
    }
    
    /**
     * 根据主键查询相应实体
     *
     * @param ids
     * @return
     */
    public List<T> findAll(ID[] ids) {
    	return this.baseRepository.findAll(ids);
    }
    
    /**
     * 根据主键查询相应实体
     *
     * @param ids
     * @return
     */
    public List<T> findAll(Iterable<ID> ids) {
    	return this.baseRepository.findAll(ids);
    } 

    /**
     * 按照顺序查询所有实体
     *
     * @param sort
     * @return
     */
    public List<T> findAll(Sort sort) {
        return baseRepository.findAll(sort);
    }

    /**
     * 分页及排序查询实体
     *
     * @param pageable 分页及排序数据
     * @return
     */
    public Page<T> findAll(Pageable pageable) {
        return baseRepository.findAll(pageable);
    }

    /**
     * 按条件分页并排序查询实体
     *
     * @param searchable 条件
     * @return
     */
    public Page<T> findAll(Searchable searchable) {
        return baseRepository.findAll(searchable);
    }

    /**
     * 按条件不分页不排序查询实体
     *
     * @param searchable 条件
     * @return
     */
    public List<T> findAllWithNoPageNoSort(Searchable searchable) {
        searchable.removePageable();
        searchable.removeSort();
        return Lists.newArrayList(baseRepository.findAll(searchable).getContent());
    }

    /**
     * 按条件排序查询实体(不分页)
     *
     * @param searchable 条件
     * @return
     */
    public List<T> findAllWithSort(Searchable searchable) {
        searchable.removePageable();
        return Lists.newArrayList(baseRepository.findAll(searchable).getContent());
    }

    /**
     * 按条件分页并排序统计实体数量
     *
     * @param searchable 条件
     * @return
     */
    public Long count(Searchable searchable) {
        return baseRepository.count(searchable);
    }
    
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
    public List<T> findbySql(final String sql, final Object... objs) {
    	return this.baseRepository.findbySql(sql, objs);
    }
    
    @SuppressWarnings("hiding")
	public <T> List<T> findListbySql(final String sql, final Object... objs) {
    	return this.baseRepository.findListbySql(sql, objs);
    }
    
    @Transactional
    public int executeSql(String sql, List<Object> values) {
    	return this.baseRepository.executeSql(sql, values);
    }
}
