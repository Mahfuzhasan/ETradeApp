package com.etrade.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    public void testItemProperties() {
        Item item = new Item();
        item.setItemId(1);
        item.setTitle("Laptop");
        item.setDescription("Used gaming laptop");
        item.setPrice(750.0);
        item.setCountry("Canada");
        item.setProvince("Ontario");
        item.setCity("Toronto");
        item.setPostalCode("M5V1E3");
        item.setSold(true);
        item.setImagePath("images/laptop.jpg");
        item.setUserId(101);

        assertEquals(1, item.getItemId());
        assertEquals("Laptop", item.getTitle());
        assertEquals("Used gaming laptop", item.getDescription());
        assertEquals(750.0, item.getPrice());
        assertEquals("Canada", item.getCountry());
        assertEquals("Ontario", item.getProvince());
        assertEquals("Toronto", item.getCity());
        assertEquals("M5V1E3", item.getPostalCode());
        assertTrue(item.isSold());
        assertEquals("images/laptop.jpg", item.getImagePath());
        assertEquals(101, item.getUserId());
    }
}
