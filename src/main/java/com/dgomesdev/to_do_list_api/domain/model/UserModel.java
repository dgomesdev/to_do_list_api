package com.dgomesdev.to_do_list_api.domain.model;

import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class UserModel extends User {

    private final UUID userId;
    private final List<TaskModel> tasks;
    private final String email;

    private UserModel(Builder builder) {
        super(
                builder.username,
                builder.password,
                builder.userAuthorities.stream()
                        .map(UserAuthority::toGrantedAuthority)
                        .toList()
        );
        this.userId = builder.userId;
        this.email = builder.email;
        this.tasks = builder.tasks;
    }

    public static class Builder {

        @Autowired
        private PasswordEncoder passwordEncoder;

        private UUID userId;
        private String username;
        private String password = "";
        private String email;
        private Set<UserAuthority> userAuthorities;
        private List<TaskModel> tasks = List.of();

        public Builder withUserId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = passwordEncoder.encode(password);
            return this;
        }

        public Builder withEmail(String email) {
            this.email = passwordEncoder.encode(email);
            return this;
        }

        public Builder withUserAuthorities(Set<UserAuthority> userAuthorities) {
            this.userAuthorities = userAuthorities;
            return this;
        }

        public Builder fromEntity(UserEntity userEntity) {
            this.userId = userEntity.getId();
            this.username = userEntity.getUsername();
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