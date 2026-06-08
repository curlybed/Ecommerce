package com.backend.ecommerce.service;

import com.backend.ecommerce.entities.Address;
import com.backend.ecommerce.entities.User;
import com.backend.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User createUser(User user){
        User newUser = User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        return userRepository.save(newUser);
    }

    public User getUserById(String userId){
        return userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }


    @Transactional
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(String userId, User user){
        User existingUser = getUserById(userId);
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        return userRepository.save(existingUser);
    }

    @Transactional
    public User addAddressToUser(String userId, Address newAddress){
        User user = getUserById(userId);
        Address saveAddress = Address.builder()
                .houseNo(newAddress.getHouseNo())
                .bulidingName(newAddress.getBulidingName())
                .locality(newAddress.getLocality())
                .addressId(newAddress.getAddressId())
                .city(newAddress.getCity())
                .state(newAddress.getState())
                .zipCode(newAddress.getZipCode())
                .build();
        user.getAddresses().add(saveAddress);
        return userRepository.save(user);
    }

    public List<Address> getAddresses(String userId){
        User user = getUserById(userId);
        return user.getAddresses();
    }


}
