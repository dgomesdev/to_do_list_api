package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
import com.dgomesdev.to_do_list_api.data.repository.UserRepository;
import com.dgomesdev.to_do_list_api.domain.exception.UnauthorizedUserException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserModel saveUser(UserModel newUser) {
        if (newUser.getEmail().isBlank() || this.isEmailInvalid(newUser.getEmail()))
            throw new IllegalArgumentException("Invalid e-mail");

        if (userRepository.findUserByEmail(newUser.getEmail()).isPresent())
            throw new DataIntegrityViolationException("User " + newUser.getEmail() + " already exists");

        if (newUser.getPassword().isBlank())
            throw new IllegalArgumentException("Invalid password");

        var savedUser = userRepository.save(
                new UserEntity(
                        newUser.getUsername(),
                        passwordEncoder.encode(newUser.getPassword()),
                        newUser.getEmail(),
                        newUser.getAuthorities()
                )
        );

        return new UserModel.Builder()
                .fromEntity(savedUser)
                .build();
    }

    @Override
    public UserModel findUserById(UUID userId) {
        if (!userId.toString().equals(this.getUserId())) throw new UnauthorizedUserException(userId);

        return new UserModel.Builder()
                .fromEntity(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId)))
                .build();
    }

    @Override
    public UserModel findUserByEmail(String email) {
        var foundUser = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return new UserModel.Builder()
                .fromEntity(foundUser)
                .build();
    }

    @Override
    public UserModel updateUser(UserModel user) {
        if (!user.getUserId().toString().equals(this.getUserId()) && !this.getUserAuthorities().contains(UserAuthority.ADMIN))
            throw new UnauthorizedUserException(user.getUserId());

        var existingUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException(user.getUserId()));

        if (!existingUser.getUsername().equals(user.getUsername())) {
            existingUser.setUsername(user.getUsername());
        }

        if (!user.getEmail().isBlank() && !passwordEncoder.matches(user.getEmail(), existingUser.getEmail())) {
            if (this.isEmailInvalid(user.getEmail())) throw new IllegalArgumentException("Invalid e-mail");
            existingUser.setEmail(user.getEmail());
        }

        if (!user.getPassword().isBlank() && !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        var userAuthorities = user.getAuthorities()
                .stream()
                .map(userAuthority -> UserAuthority.valueOf(userAuthority.getAuthority()))
                .collect(Collectors.toSet());

        if (!existingUser.getUserAuthorities().equals(userAuthorities)) {
            existingUser.setUserAuthorities(userAuthorities);
        }

        var updatedUser = userRepository.save(existingUser);

        return new UserModel.Builder()
                .fromEntity(updatedUser)
                .build();
    }

    @Override
    public void resetPassword(UUID userId, String password) {
        var existingUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (password.isBlank()) throw new IllegalArgumentException("Invalid password");
        existingUser.setPassword(passwordEncoder.encode(password));
        System.out.println(existingUser.getPassword());
        userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(UUID userId) {
        if (!userId.toString().equals(this.getUserId())) throw new UnauthorizedUserException(userId);
        userRepository.delete(
                userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId))
        );
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new UserModel.Builder()
                .fromEntity(user)
                .build();
    }
}
