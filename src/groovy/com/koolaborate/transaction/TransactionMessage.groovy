package com.koolaborate.transaction

import com.koolaborate.error.ErrorMessage;

class TransactionMessage{
	ErrorMessage errorMessage
	
	// TODO Refactor to split out transaction inbound vs outbound
	Object transactionResponse
	Object transactionRequest
}
