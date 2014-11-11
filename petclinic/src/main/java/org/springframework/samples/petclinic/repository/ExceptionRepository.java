package org.springframework.samples.petclinic.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.ExceptionLog;

public interface ExceptionRepository {

	void log(ExceptionLog ex) throws DataAccessException;
}
