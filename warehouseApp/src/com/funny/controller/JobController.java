package com.funny.controller;

import java.util.Date;
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
import com.funny.editor.ClientEditor;
import com.funny.editor.EmployeeEditor;
import com.funny.editor.ItemEditor;
import com.funny.entity.Client;
import com.funny.entity.Employee;
import com.funny.entity.Item;
import com.funny.entity.Job;
import com.funny.service.ClientService;
import com.funny.service.EmployeeService;
import com.funny.service.ItemService;
import com.funny.service.JobService;
import com.funny.ui.JobItem;

@Controller
@RequestMapping(value="/job")
public class JobController {
	
	private static final Logger logger = Logger.getLogger(JobController.class);

	@Autowired
	private ClientService clientService;
	@Autowired
	private JobService jobService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private EmployeeService employeeService;

	private static final String CREATE_FORM = "job/createForm";
	private static final String REDIRECT_JOB = "redirect:/job/";
	private static final String UPDATE_FORM = "job/updateForm";
	private static final String REDIRECT_UPDATE = "redirect:/job/update/";
	private static final String JOB_VIEW = "job/view";
			
	@RequestMapping(method=RequestMethod.GET)
	public String getCreateForm(Model model) {
		
		logger.debug("[getCreateForm] Entering method");
		
		Job job = new Job();
		model.addAttribute(job);
		
		List<Client> clientList = clientService.getAll();
		model.addAttribute(clientList);
		
		List<Job> jobList = jobService.getAll();
		model.addAttribute(jobList);
		
		logger.debug("[getCreateForm] Leaving method");
		
		return CREATE_FORM;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String create(@Valid Job job, BindingResult result) {
		
		logger.debug("[create] Entering method");
		
		if (result.hasErrors()) {
			return CREATE_FORM;
		}
		
		jobService.put(job);
		
		logger.debug("[create] Job saved : " + Util.json(job));
		
		logger.debug("[create] Leaving method");
		
		return REDIRECT_JOB;
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public String delete(@PathVariable Long id, Model model) {
		
		logger.debug("[delete] Entering method");
		
		jobService.remove(id);
		
		logger.debug("[delete] Job removed");
		
		logger.debug("[delete] Leaving method");
		
		return REDIRECT_JOB;
	}
	
	@RequestMapping(value="/update/{id}", method=RequestMethod.GET)
	public String getUpdateForm(@PathVariable Long id, Model model) {
		
		logger.debug("[getUpdateForm] Entering method");
		
		Job job = jobService.get(id);
		
		if (job == null) {
			
			logger.debug("[getUpdateForm] job not found");
			
			return REDIRECT_JOB;
		}
		
		model.addAttribute(job);
		
		List<Item> installedItems = itemService.getItemsForJob(job);
		logger.info("[getUpdateForm] installedItems size : " + installedItems.size());
		model.addAttribute("installedItems", installedItems);
		
		List<Item> itemList = itemService.getItemsOfState(ItemState.STOC, -1);
		logger.info("[getUpdateForm] itemList size : " + itemList.size());
		itemList.removeAll(installedItems);
		logger.info("[getUpdateForm] filtered availableItems size : " + itemList.size());
		model.addAttribute("itemList", itemList);
		
		List<Client> clientList = clientService.getAll();
		logger.info("[getUpdateForm] clientList size : " + clientList.size());
		model.addAttribute(clientList);
		
		List<Employee> employeeList = employeeService.getAll();
		logger.info("[getUpdateForm] employeeList size : " + employeeList.size());
		model.addAttribute(employeeList);
		
		JobItem jobItem = new JobItem();
		model.addAttribute(jobItem);
		
		logger.debug("[getUpdateForm] Leaving method");
		
		return UPDATE_FORM;
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public String update(@Valid Job job, BindingResult result, JobItem jobItem) {
		
		logger.debug("[update] Entering method");
		
		if (result.hasErrors()) {
			return UPDATE_FORM;
		}
		
		jobService.put(job);
		
		logger.debug("[update] Leaving method");
		
		return REDIRECT_UPDATE + job.getId();
	}

	@RequestMapping(value="/addItem", method=RequestMethod.POST)
	public String addItem(@Valid JobItem jobItem, BindingResult result, Job job) {
		
		logger.debug("[addItem] Entering method");
		
		if (result.hasErrors()) {
			return UPDATE_FORM;
		}
		
		Job jobFromDb = jobService.get(jobItem.getJobId());
		
		if (jobFromDb == null) {
			
			return REDIRECT_JOB;
		}

		Item addedItem = (Item)jobItem.getItem().clone();
		
		addedItem.setJob(jobFromDb); //asignam la un job
		
		addedItem.setDescription(jobItem.getDescription());
		
		addedItem.setCount(jobItem.getCount());
		
		addedItem.setEmployee(jobItem.getEmployee());
		
		addedItem.setOutDate(new Date()); //sold date
		
		addedItem.setOutPrice(jobItem.getOutPrice());
		
		addedItem.setState(ItemState.IESIRE);
		
		logger.debug("[addItem] Adding item : " + Util.json(addedItem));
		
		itemService.saveOutgoingItem(addedItem);
		
		logger.debug("[addItem] Leaving method");
		
		return REDIRECT_UPDATE + jobFromDb.getId();
	}

	@RequestMapping(value="/removeItem/{jobId}/{itemId}", method=RequestMethod.GET)
	public String removeItem(@PathVariable Long jobId, @PathVariable Long itemId, Model model) {
		
		logger.debug("[removeItem] Entering method");
		
		Item item = itemService.get(itemId);
		
		Job job = item.getJob();
		
		if(jobId != null && jobId.equals(job.getId())) {
			
			logger.debug("[removeItem] Removing item : " + Util.json(item));
			
			itemService.removeOutgoingItem(item);
		}
		
		logger.debug("[removeItem] Leaving method");
		
		return REDIRECT_UPDATE + jobId;
	}
	
	@RequestMapping(value="{id}", method=RequestMethod.GET)
	public String getView(@PathVariable Long id, Model model) {
		
		Job job = jobService.get(id);
		
		if (job == null) {
			
			return REDIRECT_JOB;
			
		}
		
		model.addAttribute(job);
		
		List<Item> installedItems = itemService.getItemsForJob(job);
		model.addAttribute("installedItems", installedItems);
		
		return JOB_VIEW;
	}
	
	@InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Client.class, new ClientEditor(this.clientService));
		binder.registerCustomEditor(Item.class, new ItemEditor(this.itemService));
		binder.registerCustomEditor(Employee.class, new EmployeeEditor(employeeService));
    }
	
}
