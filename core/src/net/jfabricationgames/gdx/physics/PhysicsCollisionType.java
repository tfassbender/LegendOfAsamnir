package net.jfabricationgames.gdx.physics;

import com.badlogic.gdx.physics.box2d.Filter;

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
	OBSTACLE_SENSOR(PhysicsBodyCategories.CATEGORY_OBSTACLE_SENSOR, PhysicsBodyCategories.MASK_OBSTACLE_SENSOR),
	MAP_OBJECT(PhysicsBodyCategories.CATEGORY_MAP_OBJECT, PhysicsBodyCategories.MASK_MAP_OBJECT); //
	
	public final short category;
	public final short mask;
	
	private PhysicsCollisionType(short category, short mask) {
		this.category = category;
		this.mask = mask;
	}
	
	public static PhysicsCollisionType getByFilter(Filter filter) {
		for (PhysicsCollisionType type : values()) {
			if (type.category == filter.categoryBits) {
				return type;
			}
		}
		return null;
	}
}
