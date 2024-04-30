package com.dgomesdev.to_do_list_api.domain.repository;

import com.dgomesdev.to_do_list_api.domain.model.User;
import com.dgomesdev.to_do_list_api.domain.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {


    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("Should return user successfully")
    void givenValidUsername_whenFindingUserByUsername_thenReturnUser() {
        //GIVEN
        final String username = "username";
        var user = new User(
                UUID.randomUUID(),
                username,
                "email",
                "password",
                UserRole.USER
        );
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        //WHEN
        var found = userRepository.findUserByUsername(username);

        //THEN
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    @Test
    @DisplayName("Should return nothing when user doesn't exist")
    void givenInvalidUsername_whenFindingUserByUsername_thenReturnNothing() {
        //GIVEN
        String username = "";
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        //WHEN
        var result = userRepository.findUserByUsername(username);

        //THEN
        assertFalse(result.isPresent());
    }
}