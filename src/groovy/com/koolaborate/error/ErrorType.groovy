package com.koolaborate.error

import com.koolaborate.model.ValueBacked;

enum ErrorType implements ValueBacked{
	WARNING("WARNING"), ERROR("ERROR")
	
	String value
	private ErrorType(String value){ this.value = value}

	@Override
	public String getValue(){
		return null;
	}
}
