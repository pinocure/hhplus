package com.hhplus.ecommerce.user.adapter.out.persistence;

import com.hhplus.ecommerce.user.domain.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryAdapterTest {

    private final UserRepositoryAdapter repository = new UserRepositoryAdapter();

    @Test
    void findById_success() {
        Optional<User> found = repository.findById(1L);
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    @Test
    void findById_notFound() {
        Optional<User> found = repository.findById(2L);
        assertFalse(found.isPresent());
    }

    @Test
    void save_success() {
        User user = new User(2L);
        User saved = repository.save(user);
        assertEquals(2L, saved.getId());
    }

}









