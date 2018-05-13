package com.streamcoffee.apistreamcoffee;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.RequestPredicates;
//import org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunctions;

import com.streamcoffee.apistreamcoffee.handler.ProductHandler;
import com.streamcoffee.apistreamcoffee.models.Product;
import com.streamcoffee.apistreamcoffee.repositories.ProductRepository;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class ApiStreamcoffeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiStreamcoffeeApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(ProductRepository repo) {
		return args -> {
			Flux<Product> productsinFlux = Flux.just(
				new Product(null,"Big Latte", 2.99),	
				new Product(null,"Big Decaf", 2.49),
				new Product(null,"Green Tea", 1.99))
					.flatMap(repo :: save);
			
			productsinFlux.thenMany(repo.findAll())
								.subscribe(System.out::println);
			
			
		};
	}
	
	@Bean
	RouterFunction<ServerResponse> routes(ProductHandler handler){
		return RouterFunctions.nest(RequestPredicates.path("/functionalproducts"), 
				RouterFunctions.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON)
						.or(RequestPredicates.contentType(MediaType.APPLICATION_JSON)),
						RouterFunctions.route(RequestPredicates.GET("/"), handler::getProducts)
						.andRoute(RequestPredicates.method(HttpMethod.POST), handler::saveProduct)
						.andRoute(RequestPredicates.DELETE("/"), handler::deleteAllProducts)
						.andNest(RequestPredicates.path("/{id}"), 
								RouterFunctions.route(RequestPredicates.method(HttpMethod.GET), handler::getProduct)
								.andRoute(RequestPredicates.method(HttpMethod.PUT), handler::updateProduct)
								.andRoute(RequestPredicates.method(HttpMethod.DELETE), handler::deleteProduct))
						));
		
	}
	
}
