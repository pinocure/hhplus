package com.hhplus.ecommerce.user.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    @Test
    void createUser_success() {
        User user = new User(1L);
        assertEquals(1L, user.getId());
    }

}










