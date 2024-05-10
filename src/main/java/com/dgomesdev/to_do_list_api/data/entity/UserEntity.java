package com.dgomesdev.to_do_list_api.data.entity;

import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.domain.model.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity(name = "tb_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        UUID id;
        @Column(unique = true, nullable = false)
        String username;
        @Column(unique = true, nullable = false)
        String email;
        @Column(nullable = false)
        String password;
        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        UserRole userRole = UserRole.USER;

        public UserEntity(UserModel userModel) {
                this.id = userModel.id();
                this.username = userModel.username();
                this.email = userModel.email();
                this.password = userModel.password();
                this.userRole = userModel.userRole();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority(this.userRole.toString()));
        }

        @Override
        public boolean isAccountNonExpired() {
                return true;
        }

        @Override
        public boolean isAccountNonLocked() {
                return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
                return true;
        }

        @Override
        public boolean isEnabled() {
                return true;
        }
}