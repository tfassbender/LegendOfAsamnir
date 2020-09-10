package net.jfabricationgames.gdx.attack;

public class AttackConfig {
	
	public String id;
	public AttackType type = AttackType.MELEE;
	public String projectileType;
	public float delay;
	public float duration;
	public float damage;
	public float distFromCenter;
	public float hitFixtureRadius;
	public float pushForce;
}
