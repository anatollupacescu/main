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

import com.funny.basic.Util;
import com.funny.entity.Employee;
import com.funny.service.EmployeeService;

@Controller
@RequestMapping(value="/employee")
public class EmployeeController {

	private static final Logger logger = Logger.getLogger(EmployeeController.class);

	@Autowired
	private EmployeeService employeeService;
	
	private static final String CREATE_FORM = "employee/createForm";
	private static final String REDIRECT_EMPLOYEE = "redirect:/employee/";
	private static final String UPDATE_FORM = "employee/updateForm";
	
	@RequestMapping(method=RequestMethod.GET)
	public String getCreateForm(Model model) {

		logger.debug("[getCreateForm] Entering method");
		
		Employee employee = new Employee();
		model.addAttribute(employee);
		
		List<Employee> employeeList = employeeService.getAll();
		
		model.addAttribute(employeeList);
		
		return CREATE_FORM;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String create(@Valid Employee employee, BindingResult result) {
		
		logger.debug("[create] Entering method");
		
		if (result.hasErrors()) {
			return CREATE_FORM;
		}
		
		logger.debug("[create] Saving employee : " + Util.json(employee));
		
		employeeService.put(employee);
		
		logger.debug("[create] Saved employee");
		
		logger.debug("[create] Entering method");
		
		return REDIRECT_EMPLOYEE;
	}
	
	@RequestMapping(value="/update/{id}", method=RequestMethod.GET)
	public String getUpdateForm(@PathVariable Long id, Model model) {
		
		Employee employee = employeeService.get(id);
		
		if (employee == null) {
			
			logger.debug("[getUpdateForm] Entering method");
			
			return UPDATE_FORM;
		}
		
		model.addAttribute(employee);
		
		return UPDATE_FORM;
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public String update(@Valid Employee employee, BindingResult result) {
		
		logger.debug("[update] Entering method");
		
		if (result.hasErrors()) {
			return UPDATE_FORM;
		}
		
		employeeService.put(employee);
		
		logger.debug("[update] Employee updated : " + Util.json(employee));
		
		logger.debug("[delete] Leaving method");
		
		return REDIRECT_EMPLOYEE;
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public String delete(@PathVariable Long id, Model model) {

		logger.debug("[delete] Entering method");
		
		employeeService.remove(id);
		
		logger.debug("[delete] Leaving method");
		
		return REDIRECT_EMPLOYEE;
	}
	
}
