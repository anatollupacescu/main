package com.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model.User;
import com.service.IUserService;
import com.service.datastore.hector.Datastore;
import com.util.Const;

public class UserServiceImpl implements IUserService {

	public User get(String email) {
		
		User user = new User();
		
		try {
			
			List<String> columns = new ArrayList<String>(1);
			
			columns.add(User.field.name.toString());
			
			Map<String, Map<String, String>> result = Datastore.getInstance().get(Const.USER, 
					
					new String[] { User.field.name.toString() } , 
						
					new String[] { email }
			);
			
			if (result.size() > 0) {
				
				Map<String, String> userMap = result.get(email);
				
				user.setEmail(email);
				user.setName(userMap.get(User.field.name.toString()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return user;
	}

	public User authentificate(String email, String password) {
		
		User user = new User();
		
		if(email == null || password == null) return user;
		
		try {
			
			Map<String, Map<String, String>> result = Datastore.getInstance().get(Const.USER, 
					
					new String[] {
						User.field.password.toString(),
						User.field.name.toString() }, 
						
					new String[] { email }
			);
			
			if(result.size() == 0) return user;
			
			Map<String, String> userMap = result.get(email);
			
			if(userMap.size() == 0) return user;
			
			String dbPassword = userMap.get(User.field.password.toString());
			
			if (password.equals(dbPassword)) {
				user.setEmail(email);
				user.setName(userMap.get(User.field.name.toString()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return user;
	}

	public void put(User user) {
		
		try {
			
			Map<String, String> data = new HashMap<String, String>();
			
			data.put(User.field.name.toString(), user.getName());
			data.put(User.field.password.toString(), user.getPassword());
			data.put(User.field.email.toString(), user.getEmail());
			
			Datastore.getInstance().store(Const.USER, user.getEmail(), data);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void remove(User user) {
		// TODO Auto-generated method stub
		
	}

}
