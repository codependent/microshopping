package com.codependent.microshopping.product.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codependent.microshopping.product.dto.Product;
import com.codependent.microshopping.product.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsRestController {

	@Autowired
	private ProductService productService;
	
	@GetMapping
	public List<Product> getProducts(){
		return productService.getProducts();
	}
	
}
