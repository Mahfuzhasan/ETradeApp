package com.etrade.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PeopleTest {

    @Test
    public void testEmptyConstructorAndSetters() {
        People person = new People();
        person.setId(10);
        person.setName("David");

        assertEquals(10, person.getId());
        assertEquals("David", person.getName());
    }

    @Test
    public void testNameConstructor() {
        People person = new People("Joey");
        assertEquals("Joey", person.getName());
    }
}
