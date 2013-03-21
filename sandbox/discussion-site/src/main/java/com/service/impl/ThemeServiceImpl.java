package com.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.model.Theme;
import com.model.Theme.Side;
import com.model.User;
import com.service.IThemeService;
import com.service.datastore.thrift.Datastore;
import com.util.Const;
import com.util.Convert;

public class ThemeServiceImpl implements IThemeService {

	private static final String[] themeColumns = new String[] {
		Theme.field.author.toString(),
		Theme.field.content.toString(),
		Theme.field.contra.toString(),
		Theme.field.pro.toString(),
		Theme.field.parent.toString(),
		Theme.field.side.toString(),
		Theme.field.date.toString()
	};
	
	private static final String[] argumentColumns = new String[] {
		Theme.field.author.toString(),
		Theme.field.content.toString(),
		Theme.field.contra.toString(),
		Theme.field.pro.toString(),
		Theme.field.parent.toString(),
		Theme.field.side.toString(),
		Theme.field.date.toString(),
		Theme.field.argumentCount.toString()
	};
	
	public Theme getById(String id) {
		
		Theme theme = new Theme();
		
		try {
			
			Map<String, String> themeMap = Datastore.getInstance().get(Const.TABLE_THEME, id);
			
			if(themeMap == null || themeMap.size() == 0) return theme;
			
			User user = new User();
			
			user.setEmail(themeMap.get(Theme.field.author.toString()));
			theme.setId(id);
			theme.setAuthor(user);
			theme.setContent(themeMap.get(Theme.field.content.toString()));
			theme.setParent(themeMap.get(Theme.field.parent.toString()));
			theme.setDate(Convert.stringToDate(themeMap.get(Theme.field.date.toString())));
			theme.setSide(Theme.Side.valueOf(themeMap.get(Theme.field.side.toString())));
			theme.setPro(Integer.valueOf(themeMap.get(Theme.field.pro.toString())));
			theme.setContra(Integer.valueOf(themeMap.get(Theme.field.contra.toString())));

		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		
		return theme;
		
	}

	private Map<String, String> themeToMap(Theme theme) {
		
		Map<String, String> data = new HashMap<String, String>();
		
		data.put(Theme.field.author.toString(), theme.getAuthor().getEmail());
		data.put(Theme.field.content.toString(), theme.getContent());
		data.put(Theme.field.date.toString(), Convert.dateToString(theme.getDate()));
		data.put(Theme.field.contra.toString(), theme.getContra().toString());
		data.put(Theme.field.pro.toString(), theme.getPro().toString());
		data.put(Theme.field.parent.toString(), theme.getParent());
		data.put(Theme.field.side.toString(), theme.getSide().toString());
		
		return data;
	}
	
	private Map<String, String> argumentToMap(Theme theme) {
		
		Map<String, String> data = themeToMap(theme);
		data.put(Theme.field.argumentCount.toString(), theme.getArgumentCount().toString());
		
		return data;
	}
	
	private boolean update(Map<String, String> data) {
		
		if(data == null) return false;
		
		String id = data.get(Theme.field.id.toString());
		
		if(id == null) return false;
		
		try {
			
			Datastore.getInstance().store(Const.TABLE_THEME, id, data);
			
		} catch (Exception e) {
			
			return false;
			
		}
		
		return true;
	}
	
	public void putTheme(Theme theme) {
		
		try {

			/* converting theme to hash map */
			Map<String, String> data = themeToMap(theme);

			String id = Convert.themeToKey(theme);
			
			if (id != null) {

				/* adding the new id to the index */
				Map<String, String> indexData = new HashMap<String, String>();
				indexData.put(id, "");

				/* storing the index data */
				Datastore.getInstance().store(Const.THEME_INDEX, theme.getParent(), indexData);

				/* saving the entity based on its id */
				Datastore.getInstance().store(Const.TABLE_THEME, id, data);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void putArgument(Theme theme) {
		
		try {
			
			/*converting theme to hash map*/
			Map<String, String> data = argumentToMap(theme);

			String id = Convert.themeToKey(theme);
			
			if(id != null) {
				
				/*adding the new id to the index*/
				Map<String, String> indexData = new HashMap<String, String>();
				indexData.put(id, "");

				/* storing the index data */
				Datastore.getInstance().store(Const.THEME_INDEX, theme.getParent(), indexData);
				
				/*saving the entity based on its id*/
				Datastore.getInstance().store(Const.TABLE_THEME, id, data);
				
				/*retrieving sub themes count*/
				Map<String, String> indexMap = Datastore.getInstance().get(Const.THEME_INDEX, theme.getParent());
				String size = Integer.valueOf(indexMap.size()).toString();
				
				/*incrementing the argumentCount of the parent*/
				Map<String, String> parentData = new HashMap<String, String>();
				parentData.put(Theme.field.argumentCount.toString(), size);
				Datastore.getInstance().store(Const.TABLE_THEME, theme.getParent(), parentData);
				
				Theme parentTheme = getById(theme.getParent());
				updateCounters(parentTheme, theme.getSide(), 1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void updateCounters(Theme theme, Side side, int v) {

		if (theme == null || Const.DEFAULT_PARENT_ID.equals(theme.getId())) return;
		
		int pro = theme.getPro();
		int con = theme.getContra();

		Map<String, String> data = new HashMap<String, String>();
		data.put(Theme.field.id.toString(), theme.getId());

		/* increase of counter */
		if (Theme.Side.PRO.equals(side)) /* pro */{

			Integer value = pro + v;

			data.put(Theme.field.pro.toString(), value.toString());

			/*updates the counter*/
			if (update(data)) {
				
				/*checks if it became valid*/
				if (pro < con && value == con) {
					
					Theme parentTheme = getById(theme.getParent());
					
					/*increase parent counter*/
					updateCounters(parentTheme, theme.getSide(), 1);
				}
			
			}

		} else /* increment contra counter */{

			Integer value = con + v;

			data.put(Theme.field.contra.toString(), value.toString());

			if (update(data)) {
				
				if (con == pro && value > pro) /*parent theme became invalid*/ {
					
					Theme parentTheme = getById(theme.getParent());
					
					updateCounters(parentTheme, theme.getSide(), -1);
					
				}
			}
		}
	}

	public void remove(Theme theme) {
		/*TODO Add remove theme functionality*/
	}

	public List<Theme> getAll(Integer count, String afterKey) {
		
		List<Theme> returnList = new ArrayList<Theme>();
		
		String id = Const.DEFAULT_PARENT_ID;
		
		try {
			
			Map<String, String> indexKeys = Datastore.getInstance().get(Const.THEME_INDEX, id);
			
			if(indexKeys.size() > 0) {
				
				List<String> keys = new ArrayList<String>(indexKeys.keySet());
				
				Map<String, Map<String, String>> themes = Datastore.getInstance().get(Const.TABLE_THEME, themeColumns, Convert.listToStringArray(keys));
				
				if(themes.size() > 0) {
					
					Set<String> keySet = themes.keySet();
					
					for(String key : keySet) {
						
						Map<String, String> themeMap = themes.get(key);
						
						if(themeMap.size() == 0) continue;
						
						Theme theme = new Theme();
						
						User user = new User();
						user.setEmail(themeMap.get(Theme.field.author.toString()));

						theme.setId(key);
						theme.setAuthor(user);
						theme.setContent(themeMap.get(Theme.field.content.toString()));
						theme.setParent(themeMap.get(Theme.field.parent.toString()));
						theme.setDate(Convert.stringToDate(themeMap.get(Theme.field.date.toString())));
						theme.setSide(Theme.Side.valueOf(themeMap.get(Theme.field.side.toString())));
						theme.setPro(Integer.valueOf(themeMap.get(Theme.field.pro.toString())));
						theme.setContra(Integer.valueOf(themeMap.get(Theme.field.contra.toString())));
						returnList.add(theme);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnList;
		
	}

	public List<Theme> getByParentId(String id, Integer count) {
		
		List<Theme> returnList = new ArrayList<Theme>();
		
		try {
			
			Map<String, String> indexKeys = Datastore.getInstance().get(Const.THEME_INDEX, id);
			
			if(indexKeys.size() > 0) {
				
				List<String> keys = new ArrayList<String>(indexKeys.keySet());
				
				Map<String, Map<String, String>> themes = Datastore.getInstance().get(Const.TABLE_THEME, argumentColumns, Convert.listToStringArray(keys));
				
				if(themes.size() > 0) {
					
					Set<String> keySet = themes.keySet();
					
					for(String key : keySet) {
						
						Map<String, String> themeMap = themes.get(key);
						
						if(themeMap.size() == 0) continue;
						
						Theme theme = new Theme();
						
						User user = new User();
						user.setEmail(themeMap.get(Theme.field.author.toString()));

						theme.setId(key);
						theme.setAuthor(user);
						theme.setContent(themeMap.get(Theme.field.content.toString()));
						theme.setParent(themeMap.get(Theme.field.parent.toString()));
						theme.setDate(Convert.stringToDate(themeMap.get(Theme.field.date.toString())));
						theme.setSide(Theme.Side.valueOf(themeMap.get(Theme.field.side.toString())));
						theme.setPro(Integer.valueOf(themeMap.get(Theme.field.pro.toString())));
						theme.setContra(Integer.valueOf(themeMap.get(Theme.field.contra.toString())));
						theme.setArgumentCount(Integer.valueOf(themeMap.get(Theme.field.argumentCount.toString())));
						returnList.add(theme);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnList;
		
	}

}
