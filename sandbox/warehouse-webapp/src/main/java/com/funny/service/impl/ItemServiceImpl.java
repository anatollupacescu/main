package com.funny.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.funny.basic.ItemState;
import com.funny.basic.JPAResourceBean;
import com.funny.entity.Item;
import com.funny.entity.ItemType;
import com.funny.entity.Job;
import com.funny.service.ItemService;
import com.funny.ui.Report;

@SuppressWarnings("unchecked")
@Service
public class ItemServiceImpl implements ItemService {

	private static final Logger logger = Logger.getLogger(ItemServiceImpl.class);
	
	@Autowired
	protected JPAResourceBean jpaResourceBean;

	public Item get(long id) {
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
		try{
			return em.find(Item.class, id);
		}catch(Exception e) {
			logger.error("Item not found", e);
		} finally {
            em.close();
        }
		return null;
	}

	public List<Item> getItemsOfState(ItemState itemState, Integer size) {
        
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
		try {
			String q = "SELECT i FROM Item i WHERE i.state = :state ORDER BY i.inDate ASC";
			Query query = em.createQuery(q);
			if(size > 0) {
				query.setMaxResults(size);
			}
			query.setParameter(Item.fields.state.toString(), itemState);
			return (List<Item>)query.getResultList();
		}finally{
            em.close();
        }
	}
	
	public List<Item> getAll() {
        
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
		try {
			String q = "SELECT i FROM Item i ORDER BY i.inDate ASC";
			Query query = em.createQuery(q);
			return query.getResultList();
		}finally{
            em.close();
        }
	}	
	
	public List<Item> getItemsForJob(Job job) {
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
        
		try{
			String q = "SELECT i FROM Item i WHERE i.job = :job ORDER BY i.inDate ASC";
			Query query = em.createQuery(q);
			query.setParameter(Item.fields.job.toString(), job);
            return (List<Item>)query.getResultList();
		}finally{
            em.close();
        }
	}
	
	public List<Job> getJobsForItem(Item item) {
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
		
		try {
			String q = "SELECT i.job FROM Item i WHERE i.type = :type AND i.price = :price AND i.state = :state";
			Query query = em.createQuery(q);
			query.setParameter(Item.fields.type.toString(), item.getType());
			query.setParameter(Item.fields.inPrice.toString(), item.getInPrice());
			query.setParameter(Item.fields.state.toString(), ItemState.IESIRE);
			return query.getResultList();
		}catch(Exception e) {
			logger.warn("Result list is empty");
		}finally{
			em.close();
		}
		return new ArrayList<Job>();
	}

	public List<Item> getItemsForReport(Report report) {
		
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
		
		if(report == null) {
			return new ArrayList<Item>();
		}
		
        try {
        	String q = "SELECT i FROM Item i WHERE 1=1";
        	if(report.getType() != null) {
        		q += " AND i.type = :type"; 
        	}
        	if(report.getState() != null && !report.getState().equals(ItemState.ANY)) {
        		q += " AND i.state = :state"; 
        	}
        	if(report.getDateFrom() != null) {
        		if(report.getState() != null && report.getState().equals(ItemState.IESIRE)) {
        			q += " AND i.outDate >= :soldDate";
        		} else {
        			q += " AND i.inDate >= :boughtDate";
        		}
        	}
        	if(report.getDateTo() != null) {
        		if(report.getState() != null && report.getState().equals(ItemState.IESIRE)) {
        			q += " AND i.outDate <= :soldDate";
        		} else {
        			q += " AND i.inDate <= :boughtDate";
        		}
        	}
        	if(report.getClient() != null) {
        		q += " AND i.job.client = :client";
        	}
        	if(report.getEmployee() != null) {
        		q += " AND i.employee = :employee";
        	}
			Query query = em.createQuery(q);
			if(report.getType() != null) {
				query.setParameter(Item.fields.type.toString(), report.getType());
        	}
        	if(report.getState() != null && !report.getState().equals(ItemState.ANY)) {
        		query.setParameter(Item.fields.state.toString(), report.getState());
        	}
        	if(report.getDateFrom() != null) {
        		if(report.getState() != null && report.getState().equals(ItemState.IESIRE)) {
        			query.setParameter("soldDate", report.getDateFrom());
        		} else {
        			query.setParameter("boughtDate", report.getDateFrom());
        		}
        	}
        	if(report.getDateTo() != null) {
        		if(report.getState() != null && report.getState().equals(ItemState.IESIRE)) {
        			query.setParameter("soldDate", report.getDateTo());
        		} else {
        			query.setParameter("boughtDate", report.getDateTo());
        		}
        	}
        	if(report.getClient() != null) {
        		query.setParameter(Job.fields.client.toString(), report.getClient());
        	}
        	if(report.getEmployee() != null) {
        		query.setParameter(Item.fields.employee.toString(), report.getEmployee());
        	}
			return (List<Item>)query.getResultList();
        } catch (Exception e) {
        	logger.error("Could not get report", e);
    	}
        return new ArrayList<Item>();
	}

	public List<Item> getItemsOfType(ItemType type) {
        EntityManager em = jpaResourceBean.getEMF().createEntityManager();
		try {
			String q = "SELECT i FROM Item i WHERE i.type = :type";
			Query query = em.createQuery(q);
			query.setParameter(Item.fields.type.toString(), type);
			return query.getResultList();
		}catch(Exception e) {
			logger.debug("List is empty");
		}finally{
            em.close();
        }
		return new ArrayList<Item>();
	}
	
	public void remove(Long id) {
		// TODO Auto-generated method stub
	}
	
	public void saveIncomingItem(Item item) throws Exception {
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
		em.getTransaction().begin();
		try {
			Item stocItem = getStockItem(em, item);
			if (stocItem == null) {
				stocItem = saveStocItem(em, item);
				if (stocItem != null) {
					item.setState(ItemState.INTRARE);
					em.persist(item);
				} else {
					throw new Exception("Could not create stock item");
				}
			} else {
				if (increaseStoc(em, stocItem, item)) {
					em.persist(item);
				} else {
					throw new Exception("Could not increase stoc");
				}
			}

			if(em.getTransaction().isActive() && !em.getTransaction().getRollbackOnly()) {
				em.getTransaction().commit();
			}
		} catch (Exception e) {
			logger.error("Exception saving the entity", e);
			if(em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		} finally {
			em.close();
		}
	}

	public void removeIncomingItem(Long id) {
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
		try {
			em.getTransaction().begin();
			Item item = get(id);
			if(!ItemState.INTRARE.equals(item.getState())) {
				throw new Exception("Not an incoming item");
			}
			Item stocItem = getStockItem(em, item);
			if (stocItem == null) {
				throw new Exception("Could not find stock item");
			} else {
				if (decreaseStoc(em, stocItem, item)) {
					item = em.merge(item);
					em.remove(item);
					if(stocItem.getCount() == 0) {
						logger.warn("Stoc item count is zero, removing...");
						stocItem = em.merge(stocItem);
						em.remove(stocItem);
					}
				} else {
					throw new Exception("Could not decrease stoc");
				}
			}

			if(em.getTransaction().isActive() && !em.getTransaction().getRollbackOnly()) {
				em.getTransaction().commit();
			}
		} catch (Exception e) {
			logger.error("Exception removing the entity", e);
			if(em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		} finally {
			em.close();
		}
	}

	public void saveOutgoingItem(Item item) {
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
		try {
			if(!ItemState.IESIRE.equals(item.getState())) {
				throw new Exception("Not an outgoing item");
			}
			if(item.getJob() == null || item.getJob().getId() == null) {
				throw new Exception("No job assigned !");
			}
			em.getTransaction().begin();
			Item stocItem = getStockItem(em, item);
			if (stocItem == null) {
				throw new Exception("Could not find stock item");
			} else {
				if (decreaseStoc(em, stocItem, item)) {
					item = em.merge(item);
					em.persist(item);
				} else {
					throw new Exception("Could not decrease stoc");
				}
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("Exception saving the entity", e);
			if(em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		} finally {
			em.close();
		}
	}

	public void removeOutgoingItem(Item item) {
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
		try {
			if(!ItemState.IESIRE.equals(item.getState())) {
				throw new Exception("Not an outgoing item");
			}
			em.getTransaction().begin();
			Item stocItem = getStockItem(em, item);
			if (stocItem == null) {
				throw new Exception("Could not find stock item");
			} else {
				if (increaseStoc(em, stocItem, item)) {
					item = em.merge(item);
					em.remove(item);
				} else {
					throw new Exception("Could not increase stoc");
				}
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			logger.error("Exception removing the entity", e);
			if(em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		} finally {
			em.close();
		}
	}		
	
	private Item getStockItem(EntityManager em, Item item) {
        try {
        	String q = "SELECT i FROM Item i WHERE i.type = :type AND i.inPrice = :inPrice AND i.state = :state";
			Query query = em.createQuery(q);
			query.setParameter(Item.fields.type.toString(), item.getType());
			query.setParameter(Item.fields.inPrice.toString(), item.getInPrice());
			query.setParameter(Item.fields.state.toString(), ItemState.STOC);
            return (Item)query.getSingleResult();
        } catch (Exception e) {
        	logger.warn("Stoc item not found ", e);
    	}
        return null;
	}
	
	private Item saveStocItem(EntityManager em, Item item) {
		try {
			Item stocItem = (Item)item.clone();
			stocItem.setState(ItemState.STOC);
			em.persist(stocItem);
			return stocItem;
		} catch (Exception e) {
			logger.warn("Could not save stoc item", e);
		}
		return null;
	}
	
	private boolean increaseStoc(EntityManager em, Item stocItem, Item item) {
		try {
			stocItem.setCount(item.getCount() + stocItem.getCount());
			stocItem = em.merge(stocItem);
			em.persist(stocItem);
			return true;
		} catch (Exception e) {
			logger.error("Could not increase stoc", e);
		}
		return false;
	}
	
	private boolean decreaseStoc(EntityManager em, Item stocItem, Item item) {
		try {
			if(item.getCount() > stocItem.getCount()) {
				throw new Exception("Not enough items in stoc");
			}
			stocItem.setCount(stocItem.getCount() - item.getCount());
			stocItem = em.merge(stocItem);
			em.persist(stocItem);
			return true;
		} catch (Exception e) {
			logger.error("Could not decrease stoc", e);
		}
		return false;
	}

	public void put(Item object) {
		// TODO Auto-generated method stub
		
	}
}
