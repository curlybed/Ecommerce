package com.backend.ecommerce.controller;

import com.backend.ecommerce.entities.Address;
import com.backend.ecommerce.entities.User;
import com.backend.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }
    @GetMapping("/all")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable String userId, @RequestBody User user){
        return userService.updateUser(userId, user);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable String userId){
        return userService.getUserById(userId);
    }

    @PostMapping("/{userId}/address")
    public User addAddressToUser(@PathVariable String userId, @RequestBody Address address){
        return userService.addAddressToUser(userId, address);
    }

    @GetMapping("/{userId}/addresses")
    public List<Address> getAddresses(@PathVariable String userId){
        return userService.getAddresses(userId);
    }


}
