package com.funny.controller;

import java.util.List;

import javax.validation.Valid;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.funny.basic.Util;
import com.funny.entity.Client;
import com.funny.service.ClientService;

@Controller
@RequestMapping(value="/client")
public class ClientController {

	private static final Logger logger = Logger.getLogger(ClientController.class);

	@Autowired
	private ClientService clientService;
	
	private static final String CREATE_FORM = "client/createForm";
	private static final String UPDATE_FORM = "client/updateForm";
	private static final String REDIRECT_CLIENT = "redirect:/client/";
	
	@RequestMapping(method=RequestMethod.GET)
	public String getCreateForm(Model model) {
		logger.debug("[getCreateForm] Entering method");
		
		Client client = new Client();
		model.addAttribute(client);
		
		logger.debug("[getCreateForm] Using empty client : " + Util.json(client));

		List<Client> clientList = clientService.getAll();
		model.addAttribute(clientList);
		
		logger.debug("[getCreateForm] Leaving method");
		return CREATE_FORM;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String create(@Valid Client client, BindingResult result) {
		logger.debug("[create] Entering method");
		if (result.hasErrors()) {
			return CREATE_FORM;
		}
		clientService.put(client);
		logger.debug("[create] Leaving method - Saved client : " + JSONObject.fromObject(client));
		return REDIRECT_CLIENT;
	}
	
	@RequestMapping(value="/update/{id}", method=RequestMethod.GET)
	public String getUpdateForm(@PathVariable Long id, Model model) {
		logger.debug("[getUpdateForm] Entering method");
		Client client = clientService.get(id);
		if (client == null) {
			logger.debug("[getUpdateForm] Entering method");
			return UPDATE_FORM;
		}
		
		model.addAttribute(client);
		logger.debug("[getUpdateForm] Leaving method");
		return UPDATE_FORM;
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public String update(@Valid Client client, BindingResult result) {
		logger.debug("[update] Entering method");
		if (result.hasErrors()) {
			return UPDATE_FORM;
		}
		logger.debug("[update] Updating client : " + JSONObject.fromObject(client));
		clientService.put(client);
		logger.debug("[update] Leaving method");
		return REDIRECT_CLIENT;
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public String delete(@PathVariable Long id, Model model) {
		Client client = clientService.get(id);
		if (client == null) {
			logger.debug("[delete] Client not found");
			return REDIRECT_CLIENT;
		}
		clientService.remove(id);
		return REDIRECT_CLIENT;
	}
}
