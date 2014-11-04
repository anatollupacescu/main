package com.funny.editor;

import java.beans.PropertyEditorSupport;

import com.funny.entity.ItemType;
import com.funny.service.ItemTypeService;

public class ItemTypeEditor extends PropertyEditorSupport {

	private final ItemTypeService itemTypeService;
	
	public ItemTypeEditor(ItemTypeService _itemTypeService) {
		itemTypeService = _itemTypeService;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		ItemType itemType = itemTypeService.get(Long.parseLong(text));
		setValue(itemType);
	}

	@Override
	public String getAsText() {
		return null;
	}
}