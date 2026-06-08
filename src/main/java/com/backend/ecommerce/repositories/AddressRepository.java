package com.backend.ecommerce.repositories;

import com.backend.ecommerce.entities.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface AddressRepository extends MongoRepository<Address,String> {

   Optional <Address> findByAddressId(String addressId);
}
