package net.jfabricationgames.gdx.physics;

public abstract class PhysicsBodyCategories {
	
	// **********************************************************************
	// *** categories
	// **********************************************************************
	public static final short CATEGORY_PLAYER = 0x0001;
	public static final short CATEGORY_PLAYER_SENSOR = 0x0002;
	public static final short CATEGORY_PLAYER_ATTACK = 0x0004;
	public static final short CATEGORY_ENEMY = 0x0008;
	public static final short CATEGORY_ENEMY_SENSOR = 0x0010;
	public static final short CATEGORY_ENEMY_ATTACK = 0x0020;
	public static final short CATEGORY_ITEM = 0x0040;
	public static final short CATEGORY_OBSTACLE = 0x0080;
	public static final short CATEGORY_OBSTACLE_SENSOR = 0x0100;
	public static final short CATEGORY_MAP_OBJECT = 0x0200;
	public static final short CATEGORY_EXPLOSION = 0x0400;
	
	// **********************************************************************
	// *** masks
	// **********************************************************************
	public static final short MASK_EVERYTHING = -1;
	public static final short MASK_NONE = 0;
	
	public static final short MASK_PLAYER = ~(CATEGORY_PLAYER | CATEGORY_PLAYER_ATTACK | CATEGORY_PLAYER_SENSOR);
	public static final short MASK_PLAYER_SENSOR = ~(CATEGORY_PLAYER | CATEGORY_PLAYER_ATTACK | CATEGORY_PLAYER_SENSOR | CATEGORY_MAP_OBJECT);
	public static final short MASK_PLAYER_ATTACK = CATEGORY_ENEMY | CATEGORY_OBSTACLE | CATEGORY_MAP_OBJECT;
	public static final short MASK_ENEMY = ~(CATEGORY_ENEMY_ATTACK | CATEGORY_ENEMY_SENSOR);
	public static final short MASK_ENEMY_SENSOR = ~(CATEGORY_ENEMY_ATTACK | CATEGORY_PLAYER_SENSOR | CATEGORY_ENEMY_SENSOR | CATEGORY_MAP_OBJECT);
	public static final short MASK_ENEMY_ATTACK = CATEGORY_PLAYER | CATEGORY_MAP_OBJECT;
	public static final short MASK_ITEM = CATEGORY_PLAYER_SENSOR;
	public static final short MASK_OBSTACLE = CATEGORY_PLAYER | CATEGORY_PLAYER_ATTACK | CATEGORY_ENEMY | CATEGORY_EXPLOSION;
	public static final short MASK_OBSTACLE_SENSOR = CATEGORY_PLAYER;
	public static final short MASK_MAP_OBJECT = MASK_EVERYTHING;
	public static final short MASK_EXPLOSION = CATEGORY_PLAYER | CATEGORY_ENEMY | CATEGORY_OBSTACLE;
}
