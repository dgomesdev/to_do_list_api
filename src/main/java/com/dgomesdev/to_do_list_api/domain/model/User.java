package com.dgomesdev.to_do_list_api.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity(name = "tb_user")
@Getter
@Setter
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID id;
        String username;
        @Column(unique = true)
        String email;
        String password;
        @OneToMany(cascade = CascadeType.ALL)
        List<Task> tasks;

}