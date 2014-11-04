package com.funny.ui;

import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.funny.basic.ItemState;
import com.funny.entity.Client;
import com.funny.entity.Employee;
import com.funny.entity.ItemType;

public class Report {
	
	public enum fields {
		type,
		state,
		dateFrom,
		dateTo,
		client,
		employee
	}
	
	private ItemType type;
	
	private ItemState state;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(style="S-")
	private Date dateFrom;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(style="S-")
	private Date dateTo;
	
	private Client client;
	
	private Employee employee;

	//holders
	
	private List<Client> clientList;
	
	private List<Employee> employeeList;
	
	private List<ItemType> typeList;
	
	private List<ItemState> itemStateList;

	//
	
	public ItemType getType() {
		return type;
	}

	public void setType(ItemType type) {
		this.type = type;
	}

	public ItemState getState() {
		return state;
	}

	public void setState(ItemState state) {
		this.state = state;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<Client> getClientList() {
		return clientList;
	}

	public void setClientList(List<Client> clientList) {
		this.clientList = clientList;
	}

	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	public List<ItemType> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<ItemType> typeList) {
		this.typeList = typeList;
	}

	public List<ItemState> getItemStateList() {
		return itemStateList;
	}

	public void setItemStateList(List<ItemState> itemStateList) {
		this.itemStateList = itemStateList;
	}
	
}
