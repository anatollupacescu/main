package com.funny.editor;

import java.beans.PropertyEditorSupport;

import com.funny.entity.Employee;
import com.funny.service.EmployeeService;

public class EmployeeEditor extends PropertyEditorSupport {

	private final EmployeeService employeeService;
	
	public EmployeeEditor(EmployeeService _employeeService) {
		employeeService = _employeeService;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		Employee employee = employeeService.get(Long.parseLong(text));
		setValue(employee);
	}

}