package com.codependent.microshopping.stock.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codependent.microshopping.stock.dto.Product;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsRestController {

	private List<Product> products = new ArrayList<>();
	
	@GetMapping
	public List<Product> getProducts(){
		return products;
	}
	
}
