package com.funny.service.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.funny.basic.JPAResourceBean;
import com.funny.entity.ItemType;
import com.funny.service.ItemTypeService;

@SuppressWarnings("unchecked")
@Service
public final class ItemTypeServiceImpl implements ItemTypeService {

	private static final Logger logger = Logger.getLogger(ItemTypeServiceImpl.class);
	
	@Autowired
	protected JPAResourceBean jpaResourceBean;
	
	@Override
	public <T> T get(long id) {

		EntityManager em = jpaResourceBean.getEMF().createEntityManager();

		try {

			return (T) em.find(ItemType.class, id);

		} catch (Exception e) {

			logger.error("Could not get itemType", e);

		} finally {

			em.close();

		}

		return null;
	}

	@Override
	public<T> List<T> getAll() {
		
        EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
        try{
            List<T> result = (List<T>)em.createQuery("select t from ItemType t").getResultList();
            
            logger.debug("[getAll] result list size : " + result.size());
            
            return result;
        }finally{
            em.close();
        }

	}
	
	@Override
	public synchronized <T>void put(T type) {
		
        EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
        em.getTransaction().begin();
        
        try{
        	
        	em.persist(type);
        	
        	em.getTransaction().commit();
        	
        }finally{
        	
            em.close();
        }
	}
	
	@Override
	public void remove(Long id) {
		
		logger.debug("[remove(Long id)] - Entering method");
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
		em.getTransaction().begin();
		
		ItemType item = em.merge((ItemType)get(id));
		
		logger.debug("[remove(Long id)] - Item merged = " + item);		
		
        try{
        	em.remove(item);
        	
        	logger.debug("[remove(Long id)] - Item removed");
        	
        	em.getTransaction().commit();
        }finally{
            em.close();
        }
        
        logger.debug("[remove(Long id)] - Leaving method");
	}

}
