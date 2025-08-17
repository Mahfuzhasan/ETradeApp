package com.etrade.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserProperties() {
        User user = new User();
        user.setUserId(200);
        user.setUsername("mahfuz");
        user.setPassword("secure123");
        user.setEmail("mahfuz@example.com");
        user.setFirstName("Mahfuz");
        user.setLastName("Hasan");

        assertEquals(200, user.getUserId());
        assertEquals("mahfuz", user.getUsername());
        assertEquals("secure123", user.getPassword());
        assertEquals("mahfuz@example.com", user.getEmail());
        assertEquals("Mahfuz", user.getFirstName());
        assertEquals("Hasan", user.getLastName());
    }
}
