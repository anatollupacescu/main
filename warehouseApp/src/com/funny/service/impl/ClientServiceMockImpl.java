package com.funny.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.funny.entity.Client;
import com.funny.service.ClientService;

@SuppressWarnings("unchecked")
public class ClientServiceMockImpl implements ClientService {

	private static final AtomicLong idSequence = new AtomicLong();
	private static final Map clients = new ConcurrentHashMap();
	
	static {
		
		Client client1 = new Client();
		client1.setId(idSequence.get());
		client1.setName("Vasilii");
		client1.setTelephoneNo("123");
		
		Client client2 = new Client();
		client2.setId(idSequence.get());
		client2.setName("Vasilii");
		client2.setTelephoneNo("123");
		
		clients.put(idSequence.incrementAndGet(), client1);
		clients.put(idSequence.incrementAndGet(), client2);
	
	}
	
	public Client get(long parseLong) {
		return (Client)clients.get(parseLong);
	}

	public <T> List<T> getAll() {
		return new ArrayList<T>(clients.values());
	}
	
	public synchronized <T> void put(T client) {
		if(((Client)client).getId() == null) {
			((Client)client).setId(idSequence.incrementAndGet());
		}
		clients.put(((Client)client).getId(), ((Client)client));
	}

	public void remove(Long id) {
		clients.remove(id);
	}

}
