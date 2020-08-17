package net.jfabricationgames.gdx.object;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.jfabricationgames.gdx.physics.PhysicsCollisionType;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;

public enum ObjectType {
	
	BARREL("barrel", "packed/barrel_hit", "packed/barrel_break", null, // 
			new PhysicsBodyProperties().setType(BodyType.StaticBody).setSensor(false).setDensity(0).setFriction(0f).setRestitution(0)
					.setCollisionType(PhysicsCollisionType.OBSTACLE),
			new Vector2(0.3f, 0.5f), new Vector2(0.05f, -0.15f)), //
	BOX("box", "packed/box_hit", "packed/box_break", null, //
			new PhysicsBodyProperties().setType(BodyType.StaticBody).setSensor(false).setDensity(0).setFriction(0f).setRestitution(0)
					.setCollisionType(PhysicsCollisionType.OBSTACLE),
			new Vector2(0.3f, 0.5f), new Vector2(0.075f, -0.125f)), //
	CHEST("chest", "packed/chest_hit", null, "packed/chest_open", //
			new PhysicsBodyProperties().setType(BodyType.StaticBody).setSensor(false).setDensity(0).setFriction(0f).setRestitution(0)
					.setCollisionType(PhysicsCollisionType.OBSTACLE),
			new Vector2(0.4f, 0.3f), new Vector2(0.01f, -0.025f)), //
	POT("pot", "packed/pot_hit", "packed/pot_break", null, //
			new PhysicsBodyProperties().setType(BodyType.StaticBody).setSensor(false).setDensity(0).setFriction(0f).setRestitution(0)
					.setCollisionType(PhysicsCollisionType.OBSTACLE),
			new Vector2(0.3f, 0.3f), new Vector2(0.05f, -0.075f)); //
	
	private final String textureName;
	private final String animationAtlasPathHit;
	private final String animationAtlasPathBreak;
	private final String animationAtlasPathAction;
	private final PhysicsBodyProperties physicsBodyProperties;
	private final Vector2 physicsBodySizeFactor;
	private final Vector2 physicsBodyOffsetFactor;
	
	private ObjectType(String textureName, String animationAtlasPathHit, String animationAtlasPathBreak, String animationAtlasPathAction,
			PhysicsBodyProperties physicsBodyProperties, Vector2 physicsBodySizeFactor, Vector2 physicsBodyOffsetFactor) {
		this.textureName = textureName;
		this.animationAtlasPathHit = animationAtlasPathHit;
		this.animationAtlasPathBreak = animationAtlasPathBreak;
		this.animationAtlasPathAction = animationAtlasPathAction;
		this.physicsBodyProperties = physicsBodyProperties;
		this.physicsBodySizeFactor = physicsBodySizeFactor;
		this.physicsBodyOffsetFactor = physicsBodyOffsetFactor;
	}
	
	public static ObjectType getByName(String name) {
		for (ObjectType type : values()) {
			if (type.textureName.equals(name)) {
				return type;
			}
		}
		throw new IllegalArgumentException("The type '" + name + "' is unknown.");
	}
	
	public String getTextureName() {
		return textureName;
	}
	
	public boolean hasHitAnimation() {
		return animationAtlasPathHit != null;
	}
	public String getAnimationAtlasPathHit() {
		return animationAtlasPathHit;
	}
	
	public boolean hasBreakAnimation() {
		return animationAtlasPathBreak != null;
	}
	public String getAnimationAtlasPathBreak() {
		return animationAtlasPathBreak;
	}
	
	public boolean hasActionAnimation() {
		return animationAtlasPathAction != null;
	}
	public String getAnimationAtlasPathAction() {
		return animationAtlasPathAction;
	}
	
	public PhysicsBodyProperties getPhysicsBodyProperties() {
		return physicsBodyProperties;
	}
	
	public Vector2 getPhysicsBodySizeFactor() {
		return physicsBodySizeFactor;
	}
	
	public Vector2 getPhysicsBodyOffsetFactor() {
		return physicsBodyOffsetFactor;
	}
}
