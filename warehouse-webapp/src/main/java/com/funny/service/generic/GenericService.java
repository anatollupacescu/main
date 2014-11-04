package com.funny.service.generic;

import java.util.List;

public interface GenericService<T> {

	List<T> getAll();

	void put(T object);

	void remove(Long id);

	T get(long id);
}
