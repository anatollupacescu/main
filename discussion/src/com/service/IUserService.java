package com.service;

import com.model.User;

public interface IUserService {
	public User get(String email);
	public User authentificate(String email, String password);
	public void put(User user);
	public void remove(User user);
}
