package com.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.User;
import com.service.IUserService;
import com.service.impl.UserServiceImpl;
import com.util.Const;

@SuppressWarnings("serial")
public class UserController extends HttpServlet {
	
	final static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String email = (String)req.getSession().getAttribute(User.field.email.toString());
		
		if(!isEmpty(email)) {
			resp.sendRedirect("/" + Const.APPLICATION_NAME + "/" + Const.THEMES);
			return;
		}

		req.setAttribute(Const.TITLE_KEY, "Login page");
		
		try {
			req.getRequestDispatcher(Const.JSP_USER).forward(req, resp);
		} catch (ServletException e) {
			e.printStackTrace();
			resp.sendRedirect(Const.JSP_ERROR);
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String email = (String)req.getParameter(User.field.email.toString());
		String password = (String)req.getParameter(User.field.password.toString());
		
		req.setAttribute(User.field.email.toString(), email);
		
		boolean go = true;
		
		if(isEmpty(email)) {
			req.setAttribute("erroremail", "Email can not be emtpy");
			go = false;
		}
		
		if(isEmpty(password)) {
			req.setAttribute("errorpassword", "Password can not be emtpy");
			go = false;
		}
		
		String register = (String)req.getParameter("Register");
		
		if (!isEmpty(register)) { //register

			String username = (String) req.getParameter("username");
			
			req.setAttribute("username", username);
			
			if (isEmpty(username)) {
				req.setAttribute("errorusername", "Username can not be emtpy");
				go = false;
			}

			if (go) { // save
				IUserService userService = new UserServiceImpl();
				User user = userService.get(email);
				logger.debug("Got user {}", user);
				if (isEmpty(user.getName())) {
					user.setEmail(email);
					user.setName(username);
					user.setPassword(password);
					userService.put(user);
					user = userService.authentificate(email, password);
					req.getSession().setAttribute(User.field.email.toString(), user.getEmail());
				} else {
					req.setAttribute("erroremail", "Email already taken");
				}
			}

		} else if (go) { // login

				IUserService userService = new UserServiceImpl();
				User user = userService.authentificate(email, password);
				req.getSession().setAttribute(User.field.email.toString(), user.getEmail());
				if(isEmpty(user.getEmail())) {
					req.setAttribute("errorerror", "Username/password do not match");
				}
		}
		
		doGet(req, resp);
	}
	
	private static final boolean isEmpty(String str) {
		if(null == str || str.length() < 1) return true; 
			return false;
	}
}