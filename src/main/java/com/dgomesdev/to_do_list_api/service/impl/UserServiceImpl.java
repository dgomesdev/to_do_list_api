package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.data.entity.UserEntity;
import com.dgomesdev.to_do_list_api.data.repository.UserRepository;
import com.dgomesdev.to_do_list_api.domain.exception.UnauthorizedUserException;
import com.dgomesdev.to_do_list_api.domain.exception.UserNotFoundException;
import com.dgomesdev.to_do_list_api.domain.model.UserAuthority;
import com.dgomesdev.to_do_list_api.domain.model.UserModel;
import com.dgomesdev.to_do_list_api.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserModel saveUser(UserModel newUser) {
        try {
            return new UserModel(userRepository.save(new UserEntity(newUser)));
        } catch (Exception e) {
            throw new RuntimeException("Invalid user: " + e.getLocalizedMessage());
        }
    }

    @Override
    public UserModel findUserById(UUID userId) {
        if (!userId.toString().equals(this.getUserId())) throw new UnauthorizedUserException();
        return new UserModel(userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new));
    }

    @Override
    public UserModel updateUser(UserModel user, UUID userId) {
            if (!userId.toString().equals(this.getUserId())) throw new UnauthorizedUserException();
            var existingUser = userRepository.findById(user.getUserID())
                    .orElseThrow(UserNotFoundException::new);

            if (!existingUser.getUsername().equals(user.getUsername())) {
                existingUser.setUsername(user.getUsername());
            }
            if (user.getPassword() != null) {
                existingUser.setPassword(user.getPassword());
            }
            var userAuthorities = user.getAuthorities()
                    .stream()
                    .map(userAuthority -> UserAuthority.valueOf(userAuthority.getAuthority()))
                    .collect(Collectors.toSet());
            if (!existingUser.getUserAuthorities().equals(userAuthorities)) {
                existingUser.setUserAuthorities(userAuthorities);
            }

            var updatedUser = userRepository.save(existingUser);

            return new UserModel(updatedUser);
    }


    @Override
    public void deleteUser(UUID userId) {
        if (!userId.toString().equals(this.getUserId())) throw new UnauthorizedUserException();
        userRepository.delete(
                userRepository.findById(userId).orElseThrow(UserNotFoundException::new)
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findUserByUsername(username).orElseThrow();
        return new UserModel(user);
    }
}
