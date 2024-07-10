package com.codemind.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {

	@Autowired
	CurrencyExchangeProxy proxy;
	
	@GetMapping("currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion calculateCurrencyConversion(@PathVariable(name = "from") String from,
			@PathVariable(name = "to") String to, @PathVariable(name = "quantity") BigDecimal quantity) {

		HashMap<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class,
				uriVariables);

		CurrencyConversion currencyConversion = responseEntity.getBody();
		System.out.println(currencyConversion);
		BigDecimal totalAmount=quantity.multiply(currencyConversion.getConversionMultiple());
		
		return new CurrencyConversion(currencyConversion.getId(), from, to, quantity,
				currencyConversion.getConversionMultiple(),totalAmount ,
				currencyConversion.getEnvironment()+" "+" from RestTemplate");

//		return new CurrencyConversion(10001L, from, to, quantity, BigDecimal.ONE, BigDecimal.ONE, "");

	}
	
	@GetMapping("currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable(name = "from") String from,
			@PathVariable(name = "to") String to, @PathVariable(name = "quantity") BigDecimal quantity) {

		
		CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);
		System.out.println(currencyConversion);
		BigDecimal totalAmount=quantity.multiply(currencyConversion.getConversionMultiple());
		
		return new CurrencyConversion(currencyConversion.getId(), from, to, quantity,
				currencyConversion.getConversionMultiple(),totalAmount ,
				currencyConversion.getEnvironment()+" "+" using Feign");

//		return new CurrencyConversion(10001L, from, to, quantity, BigDecimal.ONE, BigDecimal.ONE, "");

	}

}
