package com.funny.ui;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.funny.entity.Employee;
import com.funny.entity.Item;

public class JobItem {

	public static enum fields {
		jobId,
		item,
		description,
		count,
		employee,
		outPrice,
		outDate
	}
	
	@NotNull
	private Long jobId;
	
	@NotNull
	private Item item;
	
	private String description;
	
	@NotNull
	private Integer count = 1;
	
	@NotNull
	private Employee employee;
	
	@NotNull
	private BigDecimal outPrice = new BigDecimal(0);
	
	@DateTimeFormat(style="S-")
	private Date outDate = new Date();

	public JobItem() {
	}
	
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public BigDecimal getOutPrice() {
		return outPrice;
	}

	public void setOutPrice(BigDecimal outPrice) {
		this.outPrice = outPrice;
	}

	public Date getOutDate() {
		return outDate;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}
	
}