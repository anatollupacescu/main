package com.funny.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class ItemType {
    
	public static enum fields {
		name
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	
	@NotNull
	@Size(min=4, max=128)
	private String name;

	public ItemType() {	}
	
	public ItemType(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}