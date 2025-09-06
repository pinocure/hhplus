package com.hhplus.ecommerce.user.service;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.user.application.port.out.UserRepository;
import com.hhplus.ecommerce.user.application.service.UserService;
import com.hhplus.ecommerce.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void getUser_success() {
        User user = new User(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> userService.getUser(1L));
    }

}












