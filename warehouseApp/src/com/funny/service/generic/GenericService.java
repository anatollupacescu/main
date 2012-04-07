package com.funny.service.generic;

import java.util.List;

public interface GenericService {

	<T>List<T> getAll();

	<T>void put(T object);

	void remove(Long id);

	<T>T get(long id);

}
