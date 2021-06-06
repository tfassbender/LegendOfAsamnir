package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.object.interactive.InteractiveAction;
import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class GameObjectTypeConfig {
	
	public GameObjectType type;
	
	public String texture;
	public String animationHit;
	public String hitSound;
	
	public float textureSizeFactorX = 1f;
	public float textureSizeFactorY = 1f;
	
	public float physicsBodySizeFactorX = 1f;
	public float physicsBodySizeFactorY = 1f;
	public float physicsBodyOffsetFactorX = 0f;
	public float physicsBodyOffsetFactorY = 0f;
	
	public BodyType bodyType = BodyType.StaticBody;
	public float density = 0f;
	public float friction = 0f;
	public float restitution = 0f;
	public PhysicsCollisionType collisionType = PhysicsCollisionType.OBSTACLE;
	public boolean isSensor = false;
	public boolean addSensor = false;
	public float sensorRadius = 0.5f;
	
	public GameObjectAction initAction; // an action that is executed after the game object was added to the map
	
	// map the default drop types to the probability to drop them. The probability sum must be <= 1.
	public ObjectMap<String, Float> drops;
	public float dropPositionOffsetX;
	public float dropPositionOffsetY;
	public boolean renderDropsAboveObject = false;
	
	//****************************************
	//*** Destroyable Objects
	//****************************************
	
	public String animationBreak;
	public String destroySound;
	public float health = 0f;
	public AttackType requiredAttackType = AttackType.ATTACK;
	
	//****************************************
	//*** Interactive Objects
	//****************************************
	
	public String animationAction;
	public String textureAfterAction;
	public boolean multipleActionExecutionsPossible = false;
	public boolean hitAnimationAfterAction = false;
	public boolean changeBodyToSensorAfterAction = false;
	public boolean interactByContact = false;
	public float interactionMarkerOffsetX = 0f;
	public float interactionMarkerOffsetY = 0f;
	public InteractiveAction interactiveAction; // references a value from the InteractiveAction enum
	
	//****************************************
	//*** Locked Objects
	//****************************************
	
	public boolean defaultLocked = true;
	public String animationActionReversed;
}
