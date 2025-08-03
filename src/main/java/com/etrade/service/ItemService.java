package com.etrade.service;

import com.etrade.dao.ItemDAO;
import com.etrade.model.Item;

import java.sql.SQLException;
import java.util.List;

public class ItemService {
    private final ItemDAO itemDAO;

    public ItemService(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    public void postItem(Item item) throws SQLException {
        itemDAO.addItem(item);
    }

    public List<Item> getAllItems() throws SQLException {
        return itemDAO.getAllItems();
    }

    public List<Item> getItemsByUser(int userId) throws SQLException {
        return itemDAO.getItemsByUser(userId);
    }

    public Item getItemDetails(int itemId) throws SQLException {
        return itemDAO.getItemById(itemId);
    }

    public void updateItem(Item item) throws SQLException {
        itemDAO.updateItem(item);
    }

    public void deleteItem(int itemId) throws SQLException {
        itemDAO.deleteItem(itemId);
    }
    public List<Item> searchItems(String keyword) throws SQLException {
        return itemDAO.searchItems(keyword);
    }

}
