package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
import com.dgomesdev.to_do_list_api.data.repository.UserRepository;
import com.dgomesdev.to_do_list_api.domain.exception.UserAlreadyExistsException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final TokenServiceImpl tokenService;

    public UserServiceImpl(UserRepository userRepository, TokenServiceImpl tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Override
    public void saveUser(UserModel userModel) {
        try {
            if (userRepository.existsByUsername(userModel.username())) throw new UserAlreadyExistsException();
            var newUserEntity = new UserEntity(userModel);
            String encryptedPassword = new BCryptPasswordEncoder().encode(newUserEntity.getPassword());
            newUserEntity.setPassword(encryptedPassword);
            userRepository.save(newUserEntity);
        } catch (Exception e) {
            throw new RuntimeException("Invalid user: " + e.getLocalizedMessage());
        }
    }

    @Override
    public UserModel findUserById(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return new UserModel(user);
    }

    @Override
    public UserModel findUserByUsername(String username) {
        var user = userRepository.findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        return new UserModel(user);
    }

    @Override
    public String updateUser(UserModel updatedUser) {
        var userToBeUpdated = userRepository.findById(updatedUser.id()).orElseThrow(UserNotFoundException::new);
        try {
            String encryptedPassword = new BCryptPasswordEncoder().encode(updatedUser.password());
            userToBeUpdated.setUsername(updatedUser.username());
            userToBeUpdated.setPassword(encryptedPassword);
            userToBeUpdated.setEmail(updatedUser.email());
            userToBeUpdated.setUserRole(updatedUser.userRole());
            userRepository.save(userToBeUpdated);
            return tokenService.generateToken(userToBeUpdated);
        } catch (Exception e) {
            throw new RuntimeException("Invalid user: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void deleteUser(UUID userId) {
            userRepository.delete(
                    userRepository.findById(userId).orElseThrow(UserNotFoundException::new)
            );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity foundUserEntity = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
        return new User(
                foundUserEntity.getUsername(),
                foundUserEntity.getPassword(),
                true,
                true,
                true,
                true,
                foundUserEntity.getAuthorities());
    }
}
