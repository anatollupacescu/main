package com.funny.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.funny.entity.ItemType;
import com.funny.service.ItemTypeService;

@Controller
@RequestMapping(value="/type")
public class ItemTypeController {

	private static final Logger logger = Logger.getLogger(ItemTypeController.class);
	
	@Autowired
	private ItemTypeService itemTypeService;
	
	private final static String CREATE_FORM = "type/createForm";
	private final static String REDIRECT_TYPE = "redirect:/type/";

	@RequestMapping(method=RequestMethod.GET)
	public String getCreateForm(Model model) {
		
		logger.debug("Entering method [getCreateForm]");
		
		ItemType itemType = new ItemType();
		model.addAttribute(itemType);
		
		List<ItemType> itemTypeList = itemTypeService.getAll();
		logger.debug("[getCreateForm] itemTypeList.size()=" + itemTypeList.size());
		
		model.addAttribute(itemTypeList);
		
		logger.debug("Leaving method [getCreateForm]");
		
		return CREATE_FORM;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String create(@Valid ItemType type, BindingResult result) {
		
		logger.debug("[create] Entering method");
		
		if (result.hasErrors()) {
			return CREATE_FORM;
		}
		
		itemTypeService.put(type);
		
		logger.debug("[create] persisted type=" + type);
		
		logger.debug("[create] Leaving method");
		
		return REDIRECT_TYPE;
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public String delete(@PathVariable Long id, Model model) {
		
		logger.debug("[create] Entering method");
		
		ItemType type = itemTypeService.get(id);
		
		if(type == null) {
			
			logger.debug("[create] Item not found, id : " + id);
			
			return REDIRECT_TYPE;
			
//		}
//		
//		List<Item> itemList = itemService.getItemsOfType(type);
//
//		if (itemList.size() > 0) {
//
//			model.addAttribute("itemList", itemList);
//
//			return WARNING_PAGE;
//
		} else {

			itemTypeService.remove(id);

			logger.debug("[create] Leaving method");

			return REDIRECT_TYPE;

		}
	}
}
