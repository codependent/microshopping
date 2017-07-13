package com.codependent.microshopping.product.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	@GetMapping("/{id}/stock")
	public Integer getProductStock(@PathVariable Integer id){
		return productService.getProductStock(id);
	}
	
	@PostMapping
	public Product postProduct(@RequestBody Product product){
		return productService.addProduct(product);
	}
	
}
