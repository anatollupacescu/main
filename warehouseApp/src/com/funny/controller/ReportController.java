package com.funny.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.funny.basic.ItemState;
import com.funny.editor.ClientEditor;
import com.funny.editor.EmployeeEditor;
import com.funny.editor.ItemTypeEditor;
import com.funny.entity.Client;
import com.funny.entity.Employee;
import com.funny.entity.Item;
import com.funny.entity.ItemType;
import com.funny.service.ClientService;
import com.funny.service.EmployeeService;
import com.funny.service.ItemService;
import com.funny.service.ItemTypeService;
import com.funny.ui.Report;

@SessionAttributes({"report"})
@Controller
@RequestMapping(value="/report")
public class ReportController {
	private static final Logger logger = Logger.getLogger(ReportController.class);

	@Autowired
	private ClientService clientService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private ItemTypeService itemTypeService;
	
	@Autowired
	private ItemService itemService;
	
	private static final String FORM_PAGE = "report/form";
	
	@SuppressWarnings("unused")
	@ModelAttribute("report")
	private Report populateReport() {
		return new Report();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String getForm(Model model, @ModelAttribute("report") Report report) {
		
		logger.debug("[getForm] Entering method");
		
		if(report.getItemStateList() == null) {
			
			List<Client> clientList = clientService.getAll();
			report.setClientList(clientList);
			
			List<Employee> employeeList = employeeService.getAll();
			report.setEmployeeList(employeeList);
			
			List<ItemType> typeList = itemTypeService.getAll();
			report.setTypeList(typeList);
			
			List<ItemState> itemStateList = new ArrayList<ItemState>();
			
			itemStateList.add(ItemState.IESIRE);
			itemStateList.add(ItemState.INTRARE);
			itemStateList.add(ItemState.STOC);
			
			report.setItemStateList(itemStateList);
			
			logger.debug("[getForm] report populated");
			
		}
		
		logger.debug("[getForm] Leaving method");
		
		return FORM_PAGE;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String submit(HttpServletRequest req, @ModelAttribute("report") @Valid Report report, BindingResult result, Model model) {
		
		if (result.hasErrors()) {
			
			logger.debug("errors found : " + result.getAllErrors());
			
			return FORM_PAGE;
			
		}
		
		logger.debug("[submit] Entering method");
		
		List<Item> reportItems = itemService.getItemsForReport(report);
		
		model.addAttribute("reportItems", reportItems);
		
		logger.debug("[submit] Leaving method");
		
		return FORM_PAGE;
	}

	@InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Client.class, new ClientEditor(this.clientService));
		binder.registerCustomEditor(Employee.class, new EmployeeEditor(this.employeeService));
		binder.registerCustomEditor(ItemType.class, new ItemTypeEditor(itemTypeService));
    }
}