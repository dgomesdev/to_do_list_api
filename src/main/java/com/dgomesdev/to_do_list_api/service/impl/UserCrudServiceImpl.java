package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.domain.model.User;
import com.dgomesdev.to_do_list_api.domain.repository.UserRepository;
import com.dgomesdev.to_do_list_api.service.UserCrudService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class UserCrudServiceImpl implements UserCrudService {

    private final UserRepository userRepository;

    public UserCrudServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User save(User user) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public User update(User updatedUser) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(updatedUser.getPassword());
        updatedUser.setPassword(encryptedPassword);
        return userRepository.save(updatedUser);
    }

    @Override
    public void delete(UUID userId) {
        userRepository.delete(
                userRepository.findById(userId).orElseThrow(NoSuchElementException::new)
        );
    }
}
