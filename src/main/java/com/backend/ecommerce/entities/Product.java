package com.backend.ecommerce.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.*;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {


    @Id
    private String productId;

    @NotNull
    @Size(min = 3, max = 40)
    private String productName;

    @NotNull
    @Size(min = 3, max = 40)
    private String productDescription;

    @NotNull
    @DecimalMin(value = "0.0")
    private Double productPrice;

    @NotNull
    private String manufacturer;

    @NotNull
    @Min(value = 0)
    private Integer productQuantity;


    @DBRef
    @JsonIgnore
    private Category category;
}
