package com.dgomesdev.to_do_list_api.data.entity;

import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.*;
import java.util.stream.Collectors;

@Entity(name = "tb_user")
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Column(unique = true, nullable = false)
    private String username;

    @Setter
    @Column(nullable = false)
    private String password;

    @Setter
    @ElementCollection(targetClass = UserAuthority.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_authorities", joinColumns = @JoinColumn(name = "user_id"))
    @Column(nullable = false)
    private Set<UserAuthority> userAuthorities;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskEntity> tasks = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false,name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public UserEntity(UserModel user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.userAuthorities = user.getAuthorities()
                .stream()
                .map(userAuthority -> UserAuthority.valueOf(userAuthority.getAuthority()))
                .collect(Collectors.toSet());
    }

    protected UserEntity() {}
}