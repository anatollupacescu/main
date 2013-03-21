package com.funny.basic;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.stereotype.Service;

@Service
public class JPAResourceBean {

	private static class SingletonHolder {
		public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("swat");
	}

	public EntityManagerFactory getEMF() {
		return SingletonHolder.emf;
	}
}
