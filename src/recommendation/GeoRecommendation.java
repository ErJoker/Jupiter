package recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;


public class GeoRecommendation {
	  public List<Item> recommendItems(String userId, double lat, double lon) {
			List<Item> recommendedItems = new ArrayList<>();
			
			// Step 1, get all favorited itemids
			DBConnection db = DBConnectionFactory.getConnection();
			Set<String> favoratedItemsIds = db.getFavoriteItemIds(userId);
			
			// Step 2, get all categories, sort by count
			Map<String, Integer> allCategories = new HashMap<>();
			for (String itemId: favoratedItemsIds) {
				Set<String> categories = db.getCategories(itemId);
				for (String category: categories) {
					allCategories.put(category, allCategories.getOrDefault(category,0)+1);
				}
			}
			//sort by count
			List<Entry<String,Integer>> categorylist = new ArrayList<>(allCategories.entrySet());
			Collections.sort(categorylist, (Entry<String,Integer> e1, Entry<String,Integer> e2) -> {
				return Integer.compare(e2.getValue(), e1.getValue());
			});
			// Step 3, search based on category, filter out favorite items
			Set<String> visitedItemIds = new HashSet<>();
			for(Entry<String,Integer> category: categorylist) {
				List<Item>items = db.searchItems(lat, lon, category.getKey());
				for (Item item: items) {
					if (!favoratedItemsIds.contains(item.getItemID()) && !visitedItemIds.contains(item.getItemID())) {
					recommendedItems.add(item);
					visitedItemIds.add(item.getItemID());
					}
				}
			}
			db.close();
			return recommendedItems;
	  }

}
