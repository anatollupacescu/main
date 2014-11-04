package com.funny.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Job {
    
	public static enum fields {
		client,
		item,
		description,
		date
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	
	@NotNull
	@ManyToOne
	private Client client;
	
	@Transient
	private Item item;
	
	@NotNull
	@Size(min=4, max=128)
	private String description;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(style="S-")
	private Date date = new Date();
	
	public Job() {
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item _item) {
		item = _item;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Job (" + client +")";
	}
	
}