package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.domain.exception.UserAlreadyExistsException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.User;
import com.dgomesdev.to_do_list_api.domain.repository.UserRepository;
import com.dgomesdev.to_do_list_api.service.UserService;
import jakarta.transaction.Transactional;
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

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void saveUser(User newUser) {
        try {
            if (userRepository.existsByUsername(newUser.getUsername())) throw new UserAlreadyExistsException();
            String encryptedPassword = new BCryptPasswordEncoder().encode(newUser.getPassword());
            newUser.setPassword(encryptedPassword);
            userRepository.save(newUser);
        } catch (Exception e) {
            throw new RuntimeException("Invalid user: " + e.getLocalizedMessage());
        }
    }

    @Override
    public User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void updateUser(User updatedUser) {
        try {
            String encryptedPassword = new BCryptPasswordEncoder().encode(updatedUser.getPassword());
            updatedUser.setPassword(encryptedPassword);
            userRepository.save(updatedUser);
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
        User foundUser = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
        return new org.springframework.security.core.userdetails.User(foundUser.getUsername(), foundUser.getPassword(), true, true, true, true, foundUser.getAuthorities());
    }
}
