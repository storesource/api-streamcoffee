package com.streamcoffee.apistreamcoffee.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.streamcoffee.apistreamcoffee.models.Product;
import com.streamcoffee.apistreamcoffee.repositories.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product")
public class ProductController {
	
	@Autowired
	ProductRepository repo;
	
	@GetMapping
	public Flux<Product> getAllProducts(){
		return repo.findAll();
	}
	
	@GetMapping("{id}")
	public Mono<ResponseEntity<Product>> getAllProducts(@PathVariable String id){
		return repo.findById(id)
				.map(product -> ResponseEntity.ok(product))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Product> saveProduct(@RequestBody Product product){
		return repo.save(product);
	}
	
	@PutMapping("{id}")
	public Mono<ResponseEntity<Product>> updateProduct(@PathVariable(value = "id") String id,
			@RequestBody Product product){
		return repo.findById(id)
			.flatMap(returnedProduct ->{
				returnedProduct.setName(product.getName());
				returnedProduct.setPrice(product.getPrice());
				return repo.save(returnedProduct);
			})
			.map(updatedproduct -> ResponseEntity.ok(updatedproduct))
			.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	
	@DeleteMapping("{id}")
	public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable String id){
		return repo.findById(id)
				.flatMap(returnedProduct ->
					 repo.delete(returnedProduct)
						.then(Mono.just(ResponseEntity.ok().<Void>build()))
				)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping
	public Mono<Void> deleteAll(){
		return repo.deleteAll();
	}
}
