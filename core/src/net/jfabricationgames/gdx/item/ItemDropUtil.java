package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.jfabricationgames.gdx.map.GameMap;

public class ItemDropUtil {
	
	public static final String DROP_ITEM_MAP_PROPERTY_KEY = "drop";
	public static final float ITEM_DROP_PICKUP_DELAY = 0.75f;
	
	public static ObjectMap<String, Float> processMapProperties(MapProperties mapProperties, ObjectMap<String, Float> defaultDrops) {
		if (mapProperties.containsKey(ItemDropUtil.DROP_ITEM_MAP_PROPERTY_KEY)) {
			String droppedItemConfig = mapProperties.get(ItemDropUtil.DROP_ITEM_MAP_PROPERTY_KEY, String.class);
			return readDroppedItemConfig(droppedItemConfig);
		}
		return defaultDrops;
	}
	
	@SuppressWarnings("unchecked")
	private static ObjectMap<String, Float> readDroppedItemConfig(String droppedItemConfig) {
		Json json = new Json();
		return json.fromJson(ObjectMap.class, Float.class, droppedItemConfig);
	}
	
	public static void dropItems(ObjectMap<String, Float> dropTypes, GameMap gameMap, float x, float y, boolean renderDropsAboveObject) {
		if (doesDropItems(dropTypes)) {
			double random = Math.random();
			float summedProbability = 0f;
			for (Entry<String, Float> entry : dropTypes.entries()) {
				String dropType = entry.key;
				float dropProbability = entry.value;
				if (random <= summedProbability + dropProbability) {
					dropItem(dropType, gameMap, x, y, renderDropsAboveObject);
					return;
				}
				summedProbability += dropProbability;
			}
		}
	}
	
	private static boolean doesDropItems(ObjectMap<String, Float> dropTypes) {
		return dropTypes != null && !dropTypes.isEmpty();
	}
	
	private static void dropItem(String type, GameMap gameMap, float x, float y, boolean renderDropsAboveObject) {
		gameMap.getItemFactory().createAndDropItem(type, x, y, renderDropsAboveObject, ItemDropUtil.ITEM_DROP_PICKUP_DELAY);
	}
}
