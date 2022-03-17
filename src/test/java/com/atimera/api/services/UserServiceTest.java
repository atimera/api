package com.atimera.api.services;

import com.atimera.api.domain.User;
import com.atimera.api.exception.domain.EmailExistsException;
import com.atimera.api.exception.domain.EmailNotFoundException;
import com.atimera.api.exception.domain.UsernameExistsException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Évite de déclarer des méthodes static
class UserServiceTest {
    @Autowired
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeAll
    void beforeAll() throws EmailNotFoundException, UsernameExistsException, EmailExistsException {
        user1 = User.builder().firstName("Hamza").lastName("TIMS").username("hamza")
                .email("hamza@atimera.com").password("hamza").build();
        user2 = User.builder().firstName("Amdiatou").lastName("TIMERA").username("amdiatou")
                .email("amdiatou@atimera.com").password("amdiatou").build();

        userService.register(
                user1.getFirstName(), user1.getLastName(), user1.getUsername(), user1.getEmail(), user1.getPassword()
        );
    }

    @BeforeEach
    void beforeEach() {
    }


    @Timeout(1)
    @Test
    void register() throws EmailNotFoundException, UsernameExistsException, EmailExistsException {
        User user = userService.register(
                user2.getFirstName(), user2.getLastName(), user2.getUsername(), user2.getEmail(), user2.getPassword()
        );
        assertNotNull(user.getId(), "Utilisateur non inscrit");
        assertThat(user.getUsername()).isEqualTo("amdiatou");
        assertThat(user.isActive()).isTrue();
        assertThat(user.isNotLocked()).isTrue();
        assertThat(user.getPassword()).isNotEqualTo("amdiatou").hasSizeGreaterThanOrEqualTo(60);

    }

    @Test
    void getUsers() {
        assertEquals(2, userService.getUsers().size(), "Il devrait y avoir 2 users en base");
    }

    @Test
    void list() {
    }

    @Test
    void findByUsername() {
        User userFound = userService.findByUsername("hamza");
        assertNotNull(userFound, "User not found by username");
        assertEquals(userFound.getEmail(), "hamza@atimera.com");
    }

    @ParameterizedTest(name = "{0} x 0 doit être égal à 0")
    @ValueSource(ints = {1, 2, 42, 1011, 5089})
    public void multiply_shouldReturnZero_ofZeroWithMultipleIntegers(int arg) {
        // Arrange -- Tout est prêt !

        // Act -- Multiplier par zéro
        int actualResult = arg * 0;

        // Assert -- ça vaut toujours zéro !
        assertEquals(0, actualResult);
    }


    @Test
    @Timeout(1)
    void findUserByEmail() {
        User userFound = userService.findUserByEmail("hamza@atimera.com");
        assertEquals(userFound.getUsername(), "hamza");
        assertEquals(userFound.getId(), 1L, "Utilisateur avec un autre Id");
    }

    @Test
    void addUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void resetPassword() throws EmailNotFoundException {
        this.userService.resetPassword("hamza@atimera.com");
    }

    @Test
    void updateProfileImage() {
    }
}