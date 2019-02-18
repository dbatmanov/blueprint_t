package com.stackabuse.example;

import org.apache.camel.Exchange;

public class MyBean {

	public boolean parseException(Exchange exchange) {
		return exchange.getProperty("CamelExceptionCaught").toString().contains("statusCode: 500");
	}
}