//package com.dgomesdev.to_do_list_api.domain.model;
//
//import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
//import lombok.Getter;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//@Getter
//public class OldUserModel extends User {
//
//    private UUID userId;
//    private List<TaskModel> tasks = List.of();
//
//    public UserModel(String username, String password, Set<UserAuthority> userAuthorities) {
//        super(
//                username,
//                password,
//                userAuthorities
//                        .stream()
//                        .map(userAuthority -> new SimpleGrantedAuthority(userAuthority.name()))
//                        .toList()
//        );
//    }
//
//    public UserModel(UserEntity userEntity) {
//        super(
//                userEntity.getUsername(),
//                userEntity.getPassword(),
//                userEntity.getUserAuthorities()
//                        .stream()
//                        .map(userAuthority -> new SimpleGrantedAuthority(userAuthority.name()))
//                        .toList()
//        );
//        this.userId = (userEntity.getId() != null) ? userEntity.getId() : UUID.randomUUID();
//        this.tasks = userEntity.getTasks().stream().map(TaskModel::new).toList();
//    }
//
//    public UserModel(UUID userId, String username, Set<UserAuthority> userAuthorities) {
//        super(
//                username,
//                "",
//                userAuthorities
//                       .stream()
//                       .map(userAuthority -> new SimpleGrantedAuthority(userAuthority.name()))
//                       .toList()
//        );
//        this.userId = userId;
//    }
//}