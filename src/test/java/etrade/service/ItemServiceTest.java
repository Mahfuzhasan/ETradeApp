package com.etrade.service;

import com.etrade.dao.ItemDAO;
import com.etrade.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemServiceTest {

    private ItemDAO itemDAO;
    private ItemService itemService;

    @BeforeEach
    public void setup() {
        itemDAO = mock(ItemDAO.class);
        itemService = new ItemService(itemDAO);
    }

    @Test
    public void testGetAllItems() throws SQLException {
        when(itemDAO.getAllItems()).thenReturn(Collections.singletonList(new Item()));
        List<Item> items = itemService.getAllItems();
        assertEquals(1, items.size());
    }

    @Test
    public void testSearchItems() throws SQLException {
        when(itemDAO.searchItems("laptop")).thenReturn(Collections.singletonList(new Item()));
        List<Item> items = itemService.searchItems("laptop");
        assertFalse(items.isEmpty());
    }
}
