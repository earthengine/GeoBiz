package me.earthengine.geobiz.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.earthengine.geobiz.database.DatabaseManager;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class CustomerContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<Customer> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<Integer, Customer> ITEM_MAP = new HashMap<>();

    static {
    }

    public static void addItem(Customer item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
}
