package com.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model.Theme;
import com.model.User;
import com.service.IThemeService;
import com.service.IUserService;
import com.service.impl.ThemeServiceImpl;
import com.service.impl.UserServiceImpl;
import com.util.Const;

@SuppressWarnings("serial")
public class ThemesController extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		IThemeService themeService = new ThemeServiceImpl();

		String deleteId = req.getParameter(Const.deleteParam);

		if (deleteId != null && !deleteId.isEmpty()) {

			try {

				Theme theme = themeService.getById(deleteId);

				IUserService userService = new UserServiceImpl();
		        User user = userService.get((String)req.getSession().getAttribute(User.field.email.toString()));
		        
				if (user.getEmail().equals(theme.getAuthor())) {

					themeService.remove(theme);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		List<Theme> themes = themeService.getAll(Const.THEMES_PER_PAGE, null);

		req.setAttribute(Theme.entityGroup, themes);
		req.setAttribute(Const.TITLE_KEY, "Discussion themes");
		
		try {
			req.getRequestDispatcher(Const.JSP_THEMES).forward(req, resp);
		} catch (ServletException e) {
			e.printStackTrace();
			resp.sendRedirect(Const.JSP_ERROR);
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		IUserService userService = new UserServiceImpl();
        User user = userService.get((String)req.getSession().getAttribute(User.field.email.toString()));
		
		if (user.getEmail() != null) {

			Theme theme = new Theme();

			String content = req.getParameter(Theme.field.content.toString());

			theme.setAuthor(user);
			theme.setDate(new Date());
			theme.setContent(content);
			theme.setPro(new Integer(0));
			theme.setContra(new Integer(0));
			theme.setParent(Const.DEFAULT_PARENT_ID);
			theme.setSide(Theme.Side.PRO);
			
			IThemeService themeService = new ThemeServiceImpl();
			themeService.putTheme(theme);
		}

		resp.sendRedirect("/" + Const.APPLICATION_NAME + "/" + Const.THEMES);
		
	}
}
