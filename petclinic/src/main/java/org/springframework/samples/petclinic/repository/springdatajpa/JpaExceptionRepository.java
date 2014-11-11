/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.repository.springdatajpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.ExceptionLog;
import org.springframework.samples.petclinic.repository.ExceptionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Using native JPA instead of Spring Data JPA here because of this query:
 * "SELECT owner FROM Owner owner left join fetch owner.pets WHERE
 * owner.lastName LIKE :lastName" See
 * https://jira.springsource.org/browse/DATAJPA-292 for more details.
 *
 * @author Michael Isvy
 */
@Repository
public class JpaExceptionRepository implements ExceptionRepository {

	private final Logger log = Logger.getLogger(JpaExceptionRepository.class);
	
	private EntityManager em = null;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.em = entityManager;
	}

	@Override
	@Transactional
	public void log(ExceptionLog ex) throws DataAccessException {
		ExceptionLog merged = this.em.merge(ex);
		log.debugv("Saved to db exception with id {}", new Object[] { merged.getId() });
	}
}
