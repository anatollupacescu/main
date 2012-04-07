package com.funny.service;

import java.util.List;

import com.funny.basic.ItemState;
import com.funny.entity.Item;
import com.funny.entity.ItemType;
import com.funny.entity.Job;
import com.funny.service.generic.GenericService;
import com.funny.ui.Report;

public interface ItemService extends GenericService {

	List<Item> getItemsForJob(Job job);

	List<Job> getJobsForItem(Item item);

	List<Item> getItemsForReport(Report report);
	
	void saveIncomingItem(Item item) throws Exception;

	void removeIncomingItem(Long id);

	void saveOutgoingItem(Item addedItem);

	void removeOutgoingItem(Item item);

	List<Item> getItemsOfType(ItemType type);
	
	List<Item> getItemsOfState(ItemState itemState, Integer size);

}
