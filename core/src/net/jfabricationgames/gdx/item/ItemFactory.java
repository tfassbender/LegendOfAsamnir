package net.jfabricationgames.gdx.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.data.handler.MapObjectDataHandler;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.object.spawn.ItemSpawnFactory;
import net.jfabricationgames.gdx.physics.PhysicsWorld;
import net.jfabricationgames.gdx.util.FactoryUtil;
import net.jfabricationgames.gdx.util.SerializationUtil;

public class ItemFactory {
	
	private ItemFactory() {}
	
	private static final String CONFIG_FILE = "config/factory/item_factory.json";
	
	private static Config config;
	private static TextureAtlas atlas;
	private static ObjectMap<String, ItemTypeConfig> typeConfigs;
	private static ObjectMap<String, ObjectMap<String, Object>> defaultValues;
	
	protected static ItemTypeConfig defaultTypeConfig;
	
	static {
		config = FactoryUtil.loadConfig(Config.class, CONFIG_FILE);
		typeConfigs = FactoryUtil.loadTypeConfigs(config.itemTypeConfig, ItemTypeConfig.class);
		loadDefaultValues();
		AnimationManager.getInstance().loadAnimations(config.itemAnimations);
		atlas = AssetGroupManager.getInstance().get(config.itemAtlas);
	}
	
	@SuppressWarnings("unchecked")
	private static void loadDefaultValues() {
		defaultValues = new Json().fromJson(ObjectMap.class, ObjectMap.class, Gdx.files.internal(config.defaultValuesConfig));
		
		defaultTypeConfig = typeConfigs.get("__default");
		if (defaultTypeConfig == null) {
			Gdx.app.error(ItemFactory.class.getSimpleName(),
					"No default type config for items found. Add a type '__default' to 'config/items/types.json'.");
		}
	}
	
	public static void createAndDropItem(String type, float x, float y, boolean renderAboveGameObjects, float addBodyDelay) {
		createAndDropItem(type, new MapProperties(), x, y, renderAboveGameObjects, addBodyDelay);
	}
	
	public static void createAndDropItem(String type, MapProperties mapProperties, float x, float y, boolean renderAboveGameObjects,
			float addBodyDelay) {
		Item item = createItem(type, x, y, mapProperties, addBodyDelay);
		if (renderAboveGameObjects) {
			GameMapManager.getInstance().getMap().addItemAboveGameObjects(item);
		}
		else {
			GameMapManager.getInstance().getMap().addItem(item);
		}
		
		item.setPosition(new Vector2(x, y));
		MapObjectDataHandler.getInstance().addStatefulMapObject(item);
	}
	
	public static void addItemFromSavedState(ObjectMap<String, String> state) {
		boolean pickedUp = Boolean.parseBoolean(state.get("picked"));
		
		if (!pickedUp) {
			Json json = new Json();
			String type = state.get("itemName");
			Vector2 position = json.fromJson(Vector2.class, state.get("position"));
			MapProperties mapProperties = SerializationUtil.deserializeMapProperties(state.get("mapProperties"));
			
			createAndDropItem(type, mapProperties, position.x, position.y, true, ItemDropUtil.ITEM_DROP_PICKUP_DELAY);
		}
	}
	
	public static Item createItem(String name, float x, float y, MapProperties properties) {
		return createItem(name, x, y, properties, 0);
	}
	
	private static Item createItem(String name, float x, float y, MapProperties properties, float addBodyDelay) {
		ItemTypeConfig typeConfig = typeConfigs.get(name);
		if (typeConfig == null) {
			throw new IllegalStateException("No type config known for type: '" + name
					+ "'. Either the type name is wrong or you have to add it to the itemTypesConfig (see \"" + CONFIG_FILE + "\")");
		}
		
		Sprite sprite = FactoryUtil.createSprite(atlas, x, y, typeConfig.texture);
		AnimationDirector<TextureRegion> animation = FactoryUtil.createAnimation(x, y, typeConfig.animation);
		
		addDefaultProperties(name, properties);
		Item item;
		
		switch (typeConfig.type) {
			case ITEM:
				item = new Item(name, typeConfig, sprite, animation, properties);
				break;
			case EVENT_ITEM:
				item = new EventItem(name, typeConfig, sprite, animation, properties);
				break;
			case BUYABLE_ITEM:
				item = new BuyableItem(name, typeConfig, sprite, animation, properties);
				break;
			case RUNE:
				item = new RuneItem(name, typeConfig, sprite, animation, properties);
				break;
			case SPECIAL_ACTION:
				item = new SpecialActionItem(name, typeConfig, sprite, animation, properties);
				break;
			default:
				throw new IllegalStateException("Unknown ItemType \"" + typeConfig.type + "\" of object type \"" + name + "\"");
		}
		
		if (addBodyDelay > 0) {
			PhysicsWorld.getInstance().runDelayedAfterWorldStep(
					() -> item.createPhysicsBody(x * Constants.WORLD_TO_SCREEN, y * Constants.WORLD_TO_SCREEN), addBodyDelay);
		}
		else {
			item.createPhysicsBody(x * Constants.WORLD_TO_SCREEN, y * Constants.WORLD_TO_SCREEN);
		}
		
		return item;
	}
	
	private static void addDefaultProperties(String name, MapProperties properties) {
		if (defaultValues.containsKey(name)) {
			for (Entry<String, Object> entry : defaultValues.get(name).entries()) {
				if (!properties.containsKey(entry.key)) {
					properties.put(entry.key, entry.value);
				}
			}
		}
	}
	
	public static ItemSpawnFactory asInstance() {
		return new ItemFactoryInstance();
	}
	
	public static class ItemFactoryInstance implements ItemSpawnFactory {
		
		@Override
		public void createAndAddItem(String type, float x, float y, MapProperties mapProperties, boolean renderAboveGameObjects) {
			Item item = createItem(type, x, y, mapProperties);
			if (renderAboveGameObjects) {
				GameMapManager.getInstance().getMap().addItemAboveGameObjects(item);
			}
			else {
				GameMapManager.getInstance().getMap().addItem(item);
			}
		}
	}
	
	private static class Config {
		
		public String itemAtlas;
		public String itemAnimations;
		public String itemTypeConfig;
		public String defaultValuesConfig;
	}
}
