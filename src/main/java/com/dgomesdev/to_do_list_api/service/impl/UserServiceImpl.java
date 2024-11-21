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
        return new UserModel.Builder()
                .fromEntity(userRepository.save(new UserEntity(newUser)))
                .build();
    }

    @Override
    public UserModel findUserById(UUID userId) {
        if (!userId.toString().equals(this.getUserId())) throw new UnauthorizedUserException();
        return new UserModel.Builder()
                .fromEntity(userRepository.findById(userId).orElseThrow(UserNotFoundException::new))
                .build();
    }

    @Override
    public UserModel updateUser(UUID userId, UserModel user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (!userId.toString().equals(this.getUserId())) throw new UnauthorizedUserException();

        var existingUser = userRepository.findById(userId)
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

        return new UserModel.Builder()
                .fromEntity(updatedUser)
                .build();
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
        var user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new UserModel.Builder()
                .fromEntity(user)
                .build();
    }
}
