package com.backend.ecommerce.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String userId;

    private String name;
    @Indexed(unique = true)
    @NotNull
    private String email;

    @NotNull
    private String password;

    @Builder.Default
    private boolean emailVerified = false;

    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @DBRef
    private Cart cart;

}
