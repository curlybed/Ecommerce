package com.backend.ecommerce.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "addresses")

public class Address {



    @Id
    private String addressId ;
    @NotBlank(message = "House number is required")
    @Pattern(regexp = "^[A-Za-z0-9\\s-]{3,}", message = "House number is not valid")
    private String houseNo;

    @NotBlank(message = "Building name is required")
    @Pattern(regexp = "^[A-Za-z0-9\\s-]{3,}", message = "Building name is not valid")
    private String bulidingName;
    @NotBlank(message = "Locality is required")
    @Pattern(regexp = "^[A-Za-z0-9\\s-]{3,}", message = "Locality is not valid")
    private String locality;
    @NotBlank(message = "City name is required to fill")
    @Pattern(regexp = "^[A-Za-z\\s-]{2,}", message = "City Name is not valid")
    private String city;
    @NotBlank(message = "State is required to fill")
    @Pattern(regexp = "^[A-Za-z\\s-]{2,}", message = "State Name is not valid")
    private String state;
    @NotBlank(message = "Zip code is required to fill")
    @Pattern(regexp = "^[0-9\\s-]{6}", message = "ZipCode is not valid")
    private String zipCode;


}
