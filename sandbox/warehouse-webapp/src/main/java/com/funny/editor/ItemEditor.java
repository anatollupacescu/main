package com.funny.editor;

import java.beans.PropertyEditorSupport;

import org.apache.log4j.Logger;

import com.funny.entity.Item;
import com.funny.service.ItemService;

public class ItemEditor extends PropertyEditorSupport {

	private final ItemService itemService;
	private static final Logger logger = Logger.getLogger(ItemEditor.class);
	
	public ItemEditor(ItemService _itemService) {
		itemService = _itemService;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		Item item = itemService.get(Long.parseLong(text));
		logger.warn("setAstext item: " + item);
		setValue(item);
	}

	@Override
	public String getAsText() {
		return null;
	}
}