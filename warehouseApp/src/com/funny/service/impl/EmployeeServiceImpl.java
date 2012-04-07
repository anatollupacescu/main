package com.funny.service.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.funny.basic.JPAResourceBean;
import com.funny.entity.Employee;
import com.funny.service.EmployeeService;

@SuppressWarnings("unchecked")
@Service
public class EmployeeServiceImpl implements EmployeeService {

	private static final Logger logger = Logger.getLogger(EmployeeServiceImpl.class);
	
	@Autowired
	protected JPAResourceBean jpaResourceBean;
	
	@Override
	public Employee get(long id) {
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
		try{
            
			return (Employee) em.find(Employee.class, id);
        
		}catch(Exception e) {
			
			logger.error("Could not get employee", e);
			
		}finally{
            em.close();
        }
		
		return null;
		
	}

	@Override
	public List<Employee> getAll() {
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
        try{
            List<Employee> result = em.createQuery("select e from Employee e").getResultList();
            return result;
        }finally{
            em.close();
        }
	}
	
	@Override
	public synchronized <E>void put(E employee) {
		
		logger.debug("[put] - Entering method");
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
        try{
        	
        	em.getTransaction().begin();
        	
        	E mergedEmployee = em.merge(employee);
        	
        	logger.debug("[put] - Employee merged = " + mergedEmployee);
        	
        	em.persist(mergedEmployee);
        	
        	logger.debug("[put] - Employee persisted = " + mergedEmployee);
        	
        	em.getTransaction().commit();
        	
        }finally{
            em.close();
        }
        
        logger.debug("[put] - Leaving method");
	}

	@Override
	public void remove(Long id) {
		
		logger.debug("[remove(Long id)] - Entering method");
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
        try{
        	
        	em.getTransaction().begin();
    		
        	Employee mergedEmployee = em.merge((Employee)get(id));
    		
    		logger.debug("[remove(Long id)] - employee merged = " + mergedEmployee);		
    		
        	em.remove(mergedEmployee);
        	
        	logger.debug("[remove(Long id)] - employee removed");
        	
        	em.getTransaction().commit();
        	
        }finally{
            em.close();
        }
        
        logger.debug("[remove(Long id)] - Leaving method");
	}
	
}
