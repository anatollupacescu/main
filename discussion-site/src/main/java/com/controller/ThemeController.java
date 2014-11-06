package com.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.Theme;
import com.model.User;
import com.service.IThemeService;
import com.service.IUserService;
import com.service.impl.ThemeServiceImpl;
import com.service.impl.UserServiceImpl;
import com.util.Const;
import com.util.HistoryUtils;

@SuppressWarnings("serial")
public class ThemeController extends HttpServlet {
	
	final static Logger logger = LoggerFactory.getLogger(ThemeController.class); 
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		IThemeService themeService = new ThemeServiceImpl();
		
		try {
			
			String key = (String)req.getParameter(Const.THEME_KEY);
			
			logger.debug("Received key : " + key);
			
			if(key == null) {
				resp.sendRedirect("jsp/error.jsp");
				return;
			}
			
			Theme parentTheme = themeService.getById(key);
			
			if(parentTheme.getId() == null) {
				logger.debug("Theme not found for key : " + key);
				resp.sendRedirect(Const.JSP_ERROR);
				return;
			}
			
			String history = (String) req.getParameter(Const.history);

			if (null == history) {
				HistoryUtils.update(parentTheme, req);
			}
			
			List<Theme> themes = themeService.getByParentId(key, 100);
			
			logger.debug("Themes size : " + themes.size());
			
			req.setAttribute(Theme.field.id.toString(), parentTheme);
			req.setAttribute(Theme.entityGroup, themes);
			req.setAttribute(Const.TITLE_KEY, parentTheme.getContent());
			
			req.getRequestDispatcher(Const.JSP_THEME + "?" + Theme.field.id.toString() + "=" + parentTheme.getId()).forward(req, resp);
			
			} catch (Exception e) {
				e.printStackTrace();
				resp.sendRedirect(Const.JSP_ERROR);
			}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		IUserService userService = new UserServiceImpl();
        User user = userService.get((String)req.getSession().getAttribute(User.field.email.toString()));
        String themeId = req.getParameter(Theme.field.id.toString());
        
        if(user.getEmail() != null) {
        	
    		IThemeService themeService = new ThemeServiceImpl();
    		
            String content = req.getParameter(Theme.field.content.toString());
            String side = req.getParameter(Theme.field.side.toString());

    		Theme theme = new Theme();
    		
    		theme.setParent(themeId);
    		theme.setAuthor(user);
    		theme.setDate(new Date());
    		theme.setContent(content);
    		theme.setPro(new Integer(0));
    		theme.setContra(new Integer(0));
    		theme.setSide(Theme.Side.valueOf(side));
    		theme.setArgumentCount(0);
    		
    		themeService.putArgument(theme);
    		
        }
        
        resp.sendRedirect("/" + Const.APPLICATION_NAME + "/" + Const.THEME + "?" + Const.THEME_KEY + "=" + themeId);
        
    }
}
