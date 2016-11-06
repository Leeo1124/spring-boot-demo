//package com.leeo.common.repository.support;
//
//import java.io.Serializable;
//
//import javax.persistence.EntityManager;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.support.JpaEntityInformation;
//import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
//import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
//import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
//import org.springframework.data.repository.core.RepositoryInformation;
//import org.springframework.data.repository.core.RepositoryMetadata;
//import org.springframework.data.repository.core.support.RepositoryFactorySupport;
//import org.springframework.data.repository.query.EvaluationContextProvider;
//import org.springframework.data.repository.query.QueryLookupStrategy;
//import org.springframework.data.repository.query.QueryLookupStrategy.Key;
//import org.springframework.util.StringUtils;
//
//import com.leeo.common.repository.BaseRepository;
//import com.leeo.common.repository.callback.SearchCallback;
//import com.leeo.common.repository.support.annotation.SearchableQuery;
//
///**
// * 基础Repostory简单实现 factory bean 请参考 spring-data-jpa-reference [1.4.2. Adding
// * custom behaviour to all repositories]
// */
//public class BaseRepositoryFactoryBean<R extends JpaRepository<M, ID>, M, ID extends Serializable>
//		extends JpaRepositoryFactoryBean<R, M, ID> {
//
//	public BaseRepositoryFactoryBean() {
//	}
//
//	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
//		return new BaseRepositoryFactory(entityManager);
//	}
//}
//
//class BaseRepositoryFactory<M, ID extends Serializable> extends JpaRepositoryFactory {
//
//	private EntityManager entityManager;
//
//	public BaseRepositoryFactory(EntityManager entityManager) {
//		super(entityManager);
//		this.entityManager = entityManager;
//	}
//
//	@Override
//    protected SimpleJpaRepository<?, ?> getTargetRepository(
//			RepositoryInformation information) {
//		Class<?> repositoryInterface = information.getRepositoryInterface();
//
//        if (isBaseRepository(repositoryInterface)) {
//
//            JpaEntityInformation<M, ID> entityInformation = getEntityInformation((Class<M>)information.getDomainType());
//            BaseRepositoryImpl<M, ID> repository = new BaseRepositoryImpl<M, ID>(entityInformation, entityManager);
//
//            SearchableQuery searchableQuery = AnnotationUtils.findAnnotation(repositoryInterface, SearchableQuery.class);
//            if (searchableQuery != null) {
//                String countAllQL = searchableQuery.countAllQuery();
//                if (!StringUtils.isEmpty(countAllQL)) {
//                    repository.setCountAllQL(countAllQL);
//                }
//                String findAllQL = searchableQuery.findAllQuery();
//                if (!StringUtils.isEmpty(findAllQL)) {
//                    repository.setFindAllQL(findAllQL);
//                }
//                Class<? extends SearchCallback> callbackClass = searchableQuery.callbackClass();
//                if (callbackClass != null && callbackClass != SearchCallback.class) {
//                    repository.setSearchCallback(BeanUtils.instantiate(callbackClass));
//                }
//
//                repository.setJoins(searchableQuery.joins());
//
//            }
//
//            return repository;
//        }
//        return (SimpleJpaRepository<?, ?>) super.getTargetRepository(information);
//    }
//
//	@Override
//	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
//		if (isBaseRepository(metadata.getRepositoryInterface())) {
//			return BaseRepositoryImpl.class;
//		}
//		return super.getRepositoryBaseClass(metadata);
//	}
//
//	private boolean isBaseRepository(Class<?> repositoryInterface) {
//		return BaseRepository.class.isAssignableFrom(repositoryInterface);
//	}
//
//}