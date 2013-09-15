package com.koolaborate.error

import com.koolaborate.model.ValueBacked;

enum MessageType implements ValueBacked{
	WARNING("WARNING"), ERROR("ERROR"), INFO("INFO")
	
	String value
	private MessageType(String value){ this.value = value}

	@Override
	public String getValue(){
		return null;
	}
}
