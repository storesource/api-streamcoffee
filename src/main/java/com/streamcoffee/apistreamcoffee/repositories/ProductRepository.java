package com.streamcoffee.apistreamcoffee.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.streamcoffee.apistreamcoffee.models.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product,String>{

}
