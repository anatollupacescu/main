package com.funny.service.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.funny.basic.JPAResourceBean;
import com.funny.entity.Client;
import com.funny.service.ClientService;

@SuppressWarnings("unchecked")
@Service
public class ClientServiceImpl implements ClientService {

	private static final Logger logger = Logger.getLogger(ClientServiceImpl.class);

	@Autowired
	protected JPAResourceBean jpaResourceBean;

	public Client get(long id) {
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
		try {
			return (Client) em.find(Client.class, id);
		} catch (Exception e) {
			logger.error("Could not get client", e);
		} finally {
			em.close();
		}
		return null;
	}

	public List<Client> getAll() {
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
		try {
			return em.createQuery("select c from Client c").getResultList();
		} finally {
			em.close();
		}
	}

	public void put(Client client) {
		logger.debug("[put(Client client)] - Entering method");
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
		try {
			em.getTransaction().begin();
			Client mergedClient = em.merge(client);
			logger.debug("[put(Client client)] - Client merged = " + client);
			em.persist(mergedClient);
			logger.debug("[put(Client client)] - Client persisted = " + client);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
		logger.debug("[put(Client client)] - Leaving method");
	}

	public void remove(Long id) {
		logger.debug("[remove(Long id)] - Entering method");
		EntityManager em = jpaResourceBean.getEMF().createEntityManager();
		try {
			em.getTransaction().begin();
			Client client = em.merge(get(id));
			logger.debug("[remove(Long id)] - Client merged = " + client);
			em.remove(client);
			logger.debug("[remove(Long id)] - Item removed");
			em.getTransaction().commit();
		} finally {
			em.close();
		}
		logger.debug("[remove(Long id)] - Leaving method");
	}
}
