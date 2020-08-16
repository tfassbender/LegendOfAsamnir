package net.jfabricationgames.gdx.physics;

/**
 * Defines the category and mask bits for all body categories.
 */
public enum PhysicsCollisionType {
	
	PLAYER(PhysicsBodyCategories.CATEGORY_PLAYER, PhysicsBodyCategories.MASK_PLAYER), //
	PLAYER_SENSOR(PhysicsBodyCategories.CATEGORY_PLAYER_SENSOR, PhysicsBodyCategories.MASK_PLAYER_SENSOR), //
	PLAYER_ATTACK(PhysicsBodyCategories.CATEGORY_PLAYER_ATTACK, PhysicsBodyCategories.MASK_PLAYER_ATTACK), //
	ENEMY(PhysicsBodyCategories.CATEGORY_ENEMY, PhysicsBodyCategories.MASK_ENEMY), //
	ENEMY_SENSOR(PhysicsBodyCategories.CATEGORY_ENEMY_SENSOR, PhysicsBodyCategories.MASK_ENEMY_SENSOR), //
	ENEMY_ATTACK(PhysicsBodyCategories.CATEGORY_ENEMY_ATTACK, PhysicsBodyCategories.MASK_ENEMY_ATTACK), //
	ITEM(PhysicsBodyCategories.CATEGORY_ITEM, PhysicsBodyCategories.MASK_ITEM), //
	OBSTACLE(PhysicsBodyCategories.CATEGORY_OBSTACLE, PhysicsBodyCategories.MASK_OBSTACLE), //
	MAP_OBJECT(PhysicsBodyCategories.CATEGORY_MAP_OBJECT, PhysicsBodyCategories.MASK_MAP_OBJECT); //
	
	public final short category;
	public final short mask;
	
	private PhysicsCollisionType(short category, short mask) {
		this.category = category;
		this.mask = mask;
	}
}
