package com.funny.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.funny.entity.Employee;
import com.funny.service.EmployeeService;

@SuppressWarnings("unchecked")
public class EmployeeServiceMockImpl implements EmployeeService {

	private static final AtomicLong idSequence = new AtomicLong();
	private static final Map employees = new ConcurrentHashMap();
	
	static {
		employees.put(idSequence.incrementAndGet(), new Employee(idSequence.get(), "Radu"));
		employees.put(idSequence.incrementAndGet(), new Employee(idSequence.get(), "Valentin"));
	}
	
	public <E>E get(long parseLong) {
		return (E)employees.get(parseLong);
	}

	public <T> List<T> getAll() {
		return new ArrayList<T>(employees.values());
	}
	
	public synchronized <E> void put(E employee) {
		
		if( ((Employee)employee).getId() == null) {
			((Employee)employee).setId(idSequence.incrementAndGet());
		}
		
		employees.put(((Employee)employee).getId(), ((Employee)employee));
		
	}

	public void remove(Long id) {
		employees.remove(id);
	}
	
}
