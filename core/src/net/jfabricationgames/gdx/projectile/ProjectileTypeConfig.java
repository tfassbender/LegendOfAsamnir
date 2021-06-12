package net.jfabricationgames.gdx.projectile;

import net.jfabricationgames.gdx.attack.AttackType;

public class ProjectileTypeConfig {
	
	public AttackType attackType;
	
	public String texture;
	public String animation;
	public boolean removeAfterAnimationFinished = true;
	public float textureScale = 1f;
	public boolean textureScaleGrowing;
	public float textureInitialRotation;
	public boolean rotateTextureToMovementDirection = false;
	
	public String sound;
	
	public float range = 0f;
	public boolean removeAfterRangeExceeded = true;
	public float speed = 30f;
	public float damping = 0f;
	public float dampingAfterObjectHit = 5f;
	public float dampingAfterRangeExceeded = 0f;
	
	public float timeTillExplosion = -1f;
	public boolean multipleHitsPossible = false;
	
	public float timeActive = -1f;
}
