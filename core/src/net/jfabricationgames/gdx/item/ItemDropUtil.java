package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.object.GameObjectItemDropUtil;

public class ItemDropUtil {
	
	public static final float ITEM_DROP_PICKUP_DELAY = 0.75f;
	
	public static ObjectMap<String, Float> processMapProperties(MapProperties mapProperties, ObjectMap<String, Float> defaultDrops) {
		if (mapProperties.containsKey(Constants.MAP_PROPERTY_KEY_DROP_ITEM)) {
			String droppedItemConfig = mapProperties.get(Constants.MAP_PROPERTY_KEY_DROP_ITEM, String.class);
			return readDroppedItemConfig(droppedItemConfig);
		}
		return defaultDrops;
	}
	
	@SuppressWarnings("unchecked")
	private static ObjectMap<String, Float> readDroppedItemConfig(String droppedItemConfig) {
		Json json = new Json();
		return json.fromJson(ObjectMap.class, Float.class, droppedItemConfig);
	}
	
	public static void dropItems(ObjectMap<String, Float> dropTypes, float x, float y, boolean renderDropsAboveObject) {
		if (doesDropItems(dropTypes)) {
			double random = Math.random();
			float summedProbability = 0f;
			for (Entry<String, Float> entry : dropTypes.entries()) {
				String dropType = entry.key;
				float dropProbability = entry.value;
				if (random <= summedProbability + dropProbability) {
					dropItem(dropType, x, y, renderDropsAboveObject);
					return;
				}
				summedProbability += dropProbability;
			}
		}
	}
	
	private static boolean doesDropItems(ObjectMap<String, Float> dropTypes) {
		return dropTypes != null && !dropTypes.isEmpty();
	}
	
	private static void dropItem(String type, float x, float y, boolean renderDropsAboveObject) {
		ItemFactory.createAndDropItem(type, x, y, renderDropsAboveObject, ITEM_DROP_PICKUP_DELAY);
	}
	
	public static void dropItem(String type, MapProperties mapProperties, float x, float y, boolean renderDropsAboveObject) {
		ItemFactory.createAndDropItem(type, mapProperties, x, y, renderDropsAboveObject, ITEM_DROP_PICKUP_DELAY);
	}
	
	public static GameObjectItemDropUtil asInstance() {
		return new ItemDropUtilInstance();
	}
	
	public static class ItemDropUtilInstance implements GameObjectItemDropUtil {
		
		@Override
		public ObjectMap<String, Float> processMapProperties(MapProperties mapProperties, ObjectMap<String, Float> drops) {
			return ItemDropUtil.processMapProperties(mapProperties, drops);
		}
		
		@Override
		public void dropItem(String specialDropType, MapProperties mapProperties, float x, float y, boolean renderDropsAboveObject) {
			ItemDropUtil.dropItem(specialDropType, mapProperties, x, y, renderDropsAboveObject);
		}
		
		@Override
		public void dropItems(ObjectMap<String, Float> dropTypes, float x, float y, boolean renderDropsAboveObject) {
			ItemDropUtil.dropItems(dropTypes, x, y, renderDropsAboveObject);
		}
	}
}
