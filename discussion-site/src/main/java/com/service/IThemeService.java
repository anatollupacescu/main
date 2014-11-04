package com.service;

import java.util.List;

import com.model.Theme;

public interface IThemeService {
	public List<Theme> getAll(Integer count, String key);
	public Theme getById(String id);
	public List<Theme> getByParentId(String id, Integer count);
	public void putTheme(Theme theme);
	public void putArgument(Theme theme);
	public void remove(Theme theme);
}
