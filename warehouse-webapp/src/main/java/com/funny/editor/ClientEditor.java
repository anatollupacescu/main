package com.funny.editor;

import java.beans.PropertyEditorSupport;

import com.funny.entity.Client;
import com.funny.service.ClientService;

public class ClientEditor extends PropertyEditorSupport {

	private final ClientService clientService;

	public ClientEditor(ClientService _clientService) {
		this.clientService = _clientService;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		Client client = clientService.get(Long.parseLong(text));
		setValue(client);
	}

	@Override
	public String getAsText() {
		return null;
	}
}