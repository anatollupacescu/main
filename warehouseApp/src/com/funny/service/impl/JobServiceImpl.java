package com.funny.service.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.funny.basic.JPAResourceBean;
import com.funny.entity.Job;
import com.funny.service.JobService;

@SuppressWarnings("unchecked")
@Service
public class JobServiceImpl implements JobService {

	private static final Logger logger = Logger.getLogger(JobServiceImpl.class);
	
	@Autowired
	protected JPAResourceBean jpaResourceBean;
	
	@Override
	public <T> T get(long id) {
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
		try{
            
			return (T) em.find(Job.class, id);
        
		}finally{
            em.close();
        }
	}

	@Override
	public <T> List<T> getAll() {
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
        try{
            
        	List<T> result = em.createQuery("select j from Job j").getResultList();
            
            return result;
            
        }finally{
            em.close();
        }
	}
	
	@Override
	public synchronized <T> void put(T job) {
		
		logger.debug("[put] - Entering method");
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
        try{
        	
        	em.getTransaction().begin();
        	
        	T mergedJob = em.merge(job);
        	
        	logger.debug("[put] - Employee merged : " + mergedJob);
        	
        	em.persist(mergedJob);
        	
        	logger.debug("[put] - Employee persisted : " + mergedJob);
        	
        	em.getTransaction().commit();
        	
        }finally{
            em.close();
        }
        
        logger.debug("[put] - Leaving method");
	}

	@Override
	public void remove(Long id) {
		
		logger.debug("[remove] - Entering method");
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
        try{
        	
        	em.getTransaction().begin();
    		
        	Job job = em.merge((Job)get(id));
    		
    		logger.debug("[remove] - Employee merged : " + job);		
    		
        	em.remove(job);
        	
        	logger.debug("[remove] - Employee removed");
        	
        	em.getTransaction().commit();
        	
        }finally{
            em.close();
        }
        
        logger.debug("[remove] - Leaving method");
	}

}
