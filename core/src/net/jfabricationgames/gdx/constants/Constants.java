package net.jfabricationgames.gdx.constants;

public class Constants {
	
	public static final String GAME_VERSION = "0.3.0";
	
	public static final boolean DEBUG = false;
	
	public static final float WORLD_TO_SCREEN = 0.04f;
	public static final float SCREEN_TO_WORLD = 1f / WORLD_TO_SCREEN;
	public static final float SCENE_WIDTH = 12.80f;
	public static final float SCENE_HEIGHT = 8.20f;
	
	public static final float DENSITY_IMMOVABLE = 10_000f;
	
	public static final String DEFAULT_FONT_NAME = "vikingMedium";
	
	//the HUD uses a different scene size to make it easier to calculate in pixel units
	public static final float HUD_SCENE_FACTOR = 100f;
	public static final float HUD_SCENE_WIDTH = SCENE_WIDTH * HUD_SCENE_FACTOR;
	public static final float HUD_SCENE_HEIGHT = SCENE_HEIGHT * HUD_SCENE_FACTOR;
	
	public static final String OBJECT_NAME_ANIMAL = "animal";
	public static final String OBJECT_NAME_NPC = "npc";
	public static final String OBJECT_NAME_ENEMY = "enemy";
	public static final String OBJECT_NAME_OBJECT = "object";
	public static final String OBJECT_NAME_ITEM = "item";
	public static final String OBJECT_NAME_PLAYER = "player";
	
	public static final String MAP_PROPERTY_KEY_DROP_ITEM = "drop";
	public static final String MAP_PROPERTY_KEY_SPECIAL_DROP_TYPE = "specialDropType";
	public static final String MAP_PROPERTY_KEY_SPECIAL_DROP_MAP_PROPERTIES = "specialDropMapProperties";
	public static final String GLOBAL_VALUE_KEY_LANTERN_USED = "game_map__lantern_used";
	
	private Constants() {}
}
