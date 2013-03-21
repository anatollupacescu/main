package com.funny.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.funny.basic.ItemState;
import com.funny.basic.Util;
import com.funny.editor.ItemTypeEditor;
import com.funny.entity.Item;
import com.funny.entity.ItemType;
import com.funny.entity.Job;
import com.funny.service.ItemService;
import com.funny.service.ItemTypeService;

@Controller
@RequestMapping(value="/item")
public class ItemController {

	private static final Logger logger = Logger.getLogger(ItemController.class);
	
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private ItemTypeService itemTypeService;
	
	private static final Integer RESULTS_ON_PAGE = 20; 
	private static final String CREATE_FORM = "item/createForm";
	private static final String REDIRECT_ITEM = "redirect:/item/";
	private static final String WARNING_PAGE = "item/warningPage";
	
	@RequestMapping(method=RequestMethod.GET)
	public String getCreateForm(Model model) {
		logger.debug("[getCreateForm] Entering method");
		
		Item item = new Item();
		model.addAttribute(item);
		
		List<ItemType> itemTypeList = itemTypeService.getAll();
		model.addAttribute(itemTypeList);
		
		List<Item> itemList = itemService.getItemsOfState(ItemState.INTRARE, RESULTS_ON_PAGE);
		model.addAttribute(itemList);
		
		logger.debug("[getCreateForm] Leaving method");
		return CREATE_FORM;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String create(@Valid Item item, BindingResult result) throws Exception {
		logger.debug("[create] Entering method");
		if (result.hasErrors()) {
			return CREATE_FORM;
		}
		item.setState(ItemState.INTRARE);
		itemService.saveIncomingItem(item);
		logger.debug("[create] Leaving method - Saved item: " + Util.json(item));
		return REDIRECT_ITEM;
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public String delete(@PathVariable Long id, Model model) {
		logger.debug("[delete] Entering method");
		Item item = itemService.get(id);
		if (item == null) {
			logger.debug("[delete] item not found");
			return REDIRECT_ITEM;
		}
		List<Job> jobs = itemService.getJobsForItem(item);
		if(jobs.size() == 0) {
			itemService.removeIncomingItem(id);
			logger.debug("[delete] Leaving method");
			return REDIRECT_ITEM;
		} else {
			logger.debug("[delete] Item is in use by this jobs");
			logger.debug(jobs);
			model.addAttribute("jobList", jobs);
			return WARNING_PAGE;
		}
	}
	
	@InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(ItemType.class, new ItemTypeEditor(itemTypeService));
    }
}
