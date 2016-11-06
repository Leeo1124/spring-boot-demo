package com.leeo.common.repository.support;

import static org.springframework.data.jpa.repository.query.QueryUtils.applyAndBind;
import static org.springframework.data.jpa.repository.query.QueryUtils.getQueryString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;

import com.leeo.common.entity.LogicDeleteable;
import com.leeo.common.entity.search.Searchable;
import com.leeo.common.repository.BaseRepository;
import com.leeo.common.repository.RepositoryHelper;
import com.leeo.common.repository.callback.SearchCallback;

public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements BaseRepository<T, ID> {

	public static final String LOGIC_DELETE_ALL_QUERY_STRING = "update %s x set x.deleted=true where x in (?1)";
	public static final String FIND_QUERY_STRING = "from %s x where 1=1 ";
    public static final String COUNT_QUERY_STRING = "select count(x) from %s x where 1=1 ";
	
	private final JpaEntityInformation<T, ?> entityInformation;
	private final EntityManager entityManager;
	private Class<T> entityClass;
	private String entityName;
	private String idName;
	
	private final RepositoryHelper repositoryHelper;
	
	/**
     * 查询所有的QL
     */
    private String findAllQL;
    /**
     * 统计QL
     */
    private String countAllQL;
    
    private SearchCallback searchCallback = SearchCallback.DEFAULT;

	public BaseRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);

		// Keep the EntityManager around to used from the newly introduced
		// methods.
		this.entityInformation = entityInformation;
		this.entityManager = entityManager;
		this.entityClass = this.entityInformation.getJavaType();
		this.entityName = this.entityInformation.getEntityName();
		this.idName = this.entityInformation.getIdAttributeNames().iterator().next();
	
		repositoryHelper = new RepositoryHelper(entityClass);
		RepositoryHelper.setEntityManager(entityManager);
		
		findAllQL = String.format(FIND_QUERY_STRING, entityName);
        countAllQL = String.format(COUNT_QUERY_STRING, entityName);
	}

	@Transactional
	@Override
	public void delete(final T t) {
		if (t == null) {
			return;
		}
		if (t instanceof LogicDeleteable) {
			((LogicDeleteable) t).markDeleted();
			save(t);
		} else {
			super.delete(t);
		}
	}

	@Transactional
	@Override
	public void delete(ID[] ids) {
		if (ArrayUtils.isEmpty(ids)) {
			return;
		}
		List<T> models = new ArrayList<T>();
		for (ID id : ids) {
			T model = null;
			try {
				model = entityClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("batch delete " + entityClass + " error", e);
			}
			try {
				BeanUtils.setProperty(model, idName, id);
			} catch (Exception e) {
				throw new RuntimeException("batch delete " + entityClass + " error, can not set id", e);
			}
			models.add(model);
		}
		deleteInBatch(models);
	}

	@Transactional
	@Override
	public void deleteInBatch(final Iterable<T> entities) {
		Assert.notNull(entities, "The given Iterable of entities not be null!");

		if (!entities.iterator().hasNext()) {
			return;
		}

		boolean logicDeleteableEntity = LogicDeleteable.class.isAssignableFrom(this.entityClass);

		if (logicDeleteableEntity) {
			applyAndBind(getQueryString(LOGIC_DELETE_ALL_QUERY_STRING, entityInformation.getEntityName()), entities,
					entityManager).executeUpdate();
		} else {
			this.deleteInBatch(entities);
		}
	}

	@Override
	public Page<T> findAll(Searchable searchable) {
		List<T> list = repositoryHelper.findAll(findAllQL, searchable, searchCallback);
        long total = searchable.hasPageable() ? count(searchable) : list.size();
        return new PageImpl<T>(
                list,
                searchable.getPage(),
                total
        );
	}

	@Override
	public long count(Searchable searchable) {
		return repositoryHelper.count(countAllQL, searchable, searchCallback);
	}

}
