package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class ObjectTypeConfig {
	
	public String textureName;
	public String animationHit;
	public String animationBreak;
	public String animationAction;
	public float physicsBodySizeFactorX = 1f;
	public float physicsBodySizeFactorY = 1f;
	public float physicsBodyOffsetFactorX = 0f;
	public float physicsBodyOffsetFactorY = 0f;
	
	public BodyType bodyType = BodyType.StaticBody;
	public float density = 0f;
	public float friction = 0f;
	public float restitution = 0f;
	public PhysicsCollisionType collsitionType = PhysicsCollisionType.OBSTACLE;
	public boolean sensor = false;
	
	public String hitSound;
	public String destroySound;
	public float health = 0f;
}
