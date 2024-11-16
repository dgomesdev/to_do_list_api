package com.dgomesdev.to_do_list_api.domain.model;

import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class UserModel extends User {

    private UUID userID;
    private List<TaskModel> tasks;

    public UserModel(String username, String password, Set<UserAuthority> userAuthorities) {
        super(
                username,
                password,
                userAuthorities
                        .stream()
                        .map(userAuthority -> new SimpleGrantedAuthority(userAuthority.name()))
                        .toList()
        );
    }

    public UserModel(UserEntity userEntity) {
        super(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getUserAuthorities()
                        .stream()
                        .map(userAuthority -> new SimpleGrantedAuthority(userAuthority.name()))
                        .toList()
        );
        this.userID = userEntity.getId();
        this.tasks = userEntity.getTasks().stream().map(TaskModel::new).toList();
    }

    public UserModel(UUID userID, String username, Set<UserAuthority> userAuthorities) {
        super(
                username,
                "",
                userAuthorities
                       .stream()
                       .map(userAuthority -> new SimpleGrantedAuthority(userAuthority.name()))
                       .toList()
        );
        this.userID = userID;
    }
}