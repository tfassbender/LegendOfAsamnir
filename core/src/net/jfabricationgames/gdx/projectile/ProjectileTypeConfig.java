package net.jfabricationgames.gdx.projectile;

public class ProjectileTypeConfig {
	
	public String texture;
	public String animation;
	public boolean removeAfterAnimationFinished = true;
	public float textureScale = 1f;
	public float textureInitialRotation;
	
	public String sound;
	
	public float range = 0f;
	public boolean removeIfRangeExceeded = true;
	public float speed = 30f;
	public float damping = 0f;
	public float dampingAfterObjectHit = 5f;
	
	public float timeTillExplosion = -1f;
	public boolean multipleHitsPossible = false;
	
	public float timeActive = -1f;
}
