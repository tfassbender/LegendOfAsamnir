package net.jfabricationgames.gdx.character;

public enum CharacterAction {
	
	NONE("", null, 0f, false, true, false, 0f, null), // just stay still
	BLOCK("", null, 0f, false, false, true, 0f, null), // hold a shield to block attacks
	IDLE("dwarf_idle_", null, 0f, true, true, false, 0f, null), // staying still for some time
	RUN("dwarf_run_", null, 0f, true, true, false, 0f, null), // running in any direction
	JUMP("dwarf_jump_", "jump", 10f, true, false, true, 0f, null), // jumping with or without direction
	ATTACK("dwarf_attack_", "hit", 10f, true, false, false, 10.01f, "attack"), // attack while standing 
	ATTACK_JUMP("dwarf_attack_jump_", "hit2", 20f, true, false, true, 15.01f, "jump_attack"), // attack while running
	ATTACK_SPIN("dwarf_spin_", "hit", 10f, true, false, true, 5.01f, "spin_attack"), // spin attack
	HIT("dwarf_hit_", "damage", 0f, true, false, false, 0f, null), // dwarf got hit
	DIE("dwarf_die_", "damage", 0f, true, false, true, 0f, null), // dwarf died
	SHIELD_HIT("dwarf_shield_hit_", "shield_damage", 0f, true, false, true, 0f, null); // dwarf holding a shield (and gets hit)
	
	private final String animationPrefix;
	private final String sound;
	private final float enduranceCosts;
	private final boolean animated;
	private final boolean interruptable;
	private final boolean moveBlocking;
	private final float damage;
	private final String attack;
	
	private static final String defaultAnimationDirection = "right";
	
	private CharacterAction(String animationPrefix, String sound, float enduranceCosts, boolean animated, boolean interruptable, boolean moveBlocking,
			float damage, String attack) {
		if (interruptable && moveBlocking) {
			throw new IllegalArgumentException("An action can't be interruptable and move blocking at the same time");
		}
		
		this.animationPrefix = animationPrefix;
		this.sound = sound;
		this.enduranceCosts = enduranceCosts;
		this.animated = animated;
		this.interruptable = interruptable;
		this.moveBlocking = moveBlocking;
		this.damage = damage;
		this.attack = attack;
	}
	
	public String getAnimationName() {
		return animationPrefix + defaultAnimationDirection;
	}
	
	public String getSound() {
		return sound;
	}
	
	public float getEnduranceCosts() {
		return enduranceCosts;
	}
	
	public String getAnimationPrefix() {
		return animationPrefix;
	}
	
	public boolean isAnimated() {
		return animated;
	}
	
	public boolean isInterruptable() {
		return interruptable;
	}
	
	public boolean isMoveBlocking() {
		return moveBlocking;
	}
	
	public float getDamage() {
		return damage;
	}
	
	public boolean isAttack() {
		return attack != null;
	}
	
	public String getAttack() {
		return attack;
	}
}
