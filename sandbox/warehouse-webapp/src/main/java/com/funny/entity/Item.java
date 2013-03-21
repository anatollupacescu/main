package com.funny.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.funny.basic.ItemState;

@Entity
public class Item {
    
	public static enum fields {
		type,
		state,
		description,
		employee,
		count,
		job,
		inPrice,
		outPrice,
		inDate,
		outDate
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	private ItemType type;
	
	@Enumerated(EnumType.STRING)
	private ItemState state;
	
	private String description;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Employee employee;
	
	@NotNull
	private Integer count = 1;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Job job;
	
	@NotNull
//	@NumberFormat(style=Style.CURRENCY)
	private BigDecimal inPrice = new BigDecimal(0);
	
	@NotNull
//	@NumberFormat(style=Style.CURRENCY)
	private BigDecimal outPrice = new BigDecimal(0);
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(style="S-")
	private Date inDate = new Date();

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(style="S-")
	private Date outDate;

	public Item() {
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public BigDecimal getInPrice() {
		return inPrice;
	}

	public void setInPrice(BigDecimal inPrice) {
		this.inPrice = inPrice;
	}

	public BigDecimal getOutPrice() {
		return outPrice;
	}

	public void setOutPrice(BigDecimal outPrice) {
		this.outPrice = outPrice;
	}

	public Date getInDate() {
		return inDate;
	}

	public void setInDate(Date inDate) {
		this.inDate = inDate;
	}

	public Date getOutDate() {
		return outDate;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}

	@Override
	public Object clone() {
		
		Item item = new Item();
		
		item.setCount(count);
		item.setDescription(description);
		item.setEmployee(employee);
		item.setState(state);
		item.setJob(job);
		item.setInPrice(inPrice);
		item.setOutPrice(outPrice);
		item.setInDate(inDate);
		item.setOutDate(outDate);
		item.setType(type);
		
		return item;
	}
	
	@Override
	public String toString() {
		return type.getName();
	}
	
}