package com.streamcoffee.apistreamcoffee.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.BodyInserters;

import com.streamcoffee.apistreamcoffee.models.Product;
import com.streamcoffee.apistreamcoffee.repositories.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ProductHandler {

	@Autowired
	private ProductRepository repo;
	
	public Mono<ServerResponse> getProducts(ServerRequest request){
		Flux<Product> products = repo.findAll();
		
		return ServerResponse.ok()
				.body(products, Product.class);
	}
	
	public Mono<ServerResponse> getProduct(ServerRequest request){
		String id = request.pathVariable("id");
		Mono<Product> product = repo.findById(id);
		Mono<ServerResponse> notFoundResponse = ServerResponse.notFound().build();
		
		return product
				.flatMap(prod ->
						ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromObject(prod)))
				.switchIfEmpty(notFoundResponse);
	}
	
	public Mono<ServerResponse> saveProduct(ServerRequest request){
		Mono<Product> product = request.bodyToMono(Product.class);
		
		return product
				.flatMap(prod ->
						ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(repo.save(prod),Product.class));
	}
	
	public Mono<ServerResponse> updateProduct(ServerRequest request){
		
		String id = request.pathVariable("id");
		Mono<Product> existingproduct = repo.findById(id);
		Mono<Product> product = request.bodyToMono(Product.class);
		Mono<ServerResponse> notFoundResponse = ServerResponse.notFound().build();
		
		return product.zipWith(existingproduct,
				(prod, existingprod) -> 
					new Product(existingprod.getId(),prod.getName(),prod.getPrice()))
				
				.flatMap(prod ->
					ServerResponse.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(repo.save(prod),Product.class))
				.switchIfEmpty(notFoundResponse);
	}
	
	public Mono<ServerResponse> deleteProduct(ServerRequest request){
		String id = request.pathVariable("id");
		Mono<Product> product = repo.findById(id);
		Mono<ServerResponse> notFoundResponse = ServerResponse.notFound().build();
		
		return product
				.flatMap(prod ->
						ServerResponse.ok()
						.build(repo.delete(prod)))
				.switchIfEmpty(notFoundResponse);
	}
	
	public Mono<ServerResponse> deleteAllProducts(ServerRequest request){
		
		return ServerResponse.ok()
						.build(repo.deleteAll());
	}
}
