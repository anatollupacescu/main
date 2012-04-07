package com.funny.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.funny.entity.Job;
import com.funny.service.JobService;

@SuppressWarnings("unchecked")
public class JobServiceMockImpl implements JobService {

	private static final AtomicLong idSequence = new AtomicLong();
	private static final Map jobs = new ConcurrentHashMap();
	
	@Override
	public <T>T get(long parseLong) {
		return (T)jobs.get(parseLong);
	}

	@Override
	public <T>List<T> getAll() {
		return new ArrayList<T>(jobs.values());
	}
	
	@Override
	public synchronized <E>void put(E job) {
		if(((Job)job).getId() == null) {
			((Job)job).setId(idSequence.incrementAndGet());
		}
		jobs.put(((Job)job).getId(), ((Job)job));
	}

	@Override
	public void remove(Long id) {
		jobs.remove(id);
	}
	
}
