package com.dgomesdev.to_do_list_api.domain.model;

import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class UserModel extends User {

    private final UUID userId;
    private final List<TaskModel> tasks;

    private UserModel(Builder builder) {
        super(
                builder.username,
                builder.password,
                builder.userAuthorities.stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.name()))
                        .toList()
        );
        this.userId = builder.userId;
        this.tasks = builder.tasks != null ? List.copyOf(builder.tasks) : List.of();
    }

    public static class Builder {
        private UUID userId;
        private String username;
        private String password = ""; // Default password (empty)
        private Set<UserAuthority> userAuthorities;
        private List<TaskModel> tasks = new ArrayList<>();

        public Builder withUserId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withUserAuthorities(Set<UserAuthority> userAuthorities) {
            this.userAuthorities = userAuthorities;
            return this;
        }

        public Builder fromEntity(UserEntity userEntity) {
            this.userId = userEntity.getId() != null ? userEntity.getId() : UUID.randomUUID();
            this.username = userEntity.getUsername();
            this.password = userEntity.getPassword();
            this.userAuthorities = userEntity.getUserAuthorities();
            this.tasks = userEntity
                    .getTasks()
                    .stream()
                    .map(task -> new TaskModel.Builder().fromEntity(task).build()).toList();
            return this;
        }

        public UserModel build() {
            return new UserModel(this);
        }
    }
}