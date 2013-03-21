package com.model;

import java.util.Date;

public class Theme {
	
	public final static String THEMES_HISTORY = "THEMES_HISTORY";
	
	public final static String entityGroup = "THEMES";
//	public final static String entityTheme = "THEME";
	
	public static enum field { id, parent, date, author, content, pro, contra, side, argumentCount };
	
	public static enum Side { PRO, CONTRA };
	
	String id;
	String parent;
	User author;
	Date date;
	String content;
	Integer pro;
	Integer contra;
	Side side;
	Integer argumentCount;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getPro() {
		return pro;
	}
	public void setPro(Integer pro) {
		this.pro = pro;
	}
	public Integer getContra() {
		return contra;
	}
	public void setContra(Integer contra) {
		this.contra = contra;
	}
	public Side getSide() {
		return side;
	}
	public void setSide(Side side) {
		this.side = side;
	}
	public Integer getArgumentCount() {
		return argumentCount;
	}
	public void setArgumentCount(Integer argumentCount) {
		this.argumentCount = argumentCount;
	}
	@Override
	public String toString() {
		return "Theme [id=" + id + ", parent=" + parent + ", author=" + author
				+ ", date=" + date + ", content=" + content + ", pro=" + pro
				+ ", contra=" + contra + ", side=" + side + "]";
	}
	
}
