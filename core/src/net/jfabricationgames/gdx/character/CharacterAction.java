package net.jfabricationgames.gdx.character;

public enum CharacterAction {
	
	NONE("", null, 0f, false, true, false, 0f), // just stay still
	BLOCK("", null, 0f, false, false, true, 0f), // hold a shield to block attacks
	IDLE("dwarf_idle_", null, 0f, true, true, false, 0f), // staying still for some time
	RUN("dwarf_run_", null, 0f, true, true, false, 0f), // running in any direction
	JUMP("dwarf_jump_", "jump", 10f, true, false, true, 0f), // jumping with or without direction
	ATTACK("dwarf_attack_", "hit", 10f, true, false, false, 10.01f), // attack while standing 
	ATTACK_JUMP("dwarf_attack_jump_", "hit2", 20f, true, false, true, 15.01f), // attack while running
	ATTACK_SPIN("dwarf_spin_", "hit", 10f, true, false, true, 5.01f), // spin attack
	HIT("dwarf_hit_", "damage", 0f, true, false, false, 0f), // dwarf got hit
	DIE("dwarf_die_", "damage", 0f, true, false, true, 0f), // dwarf died
	SHIELD_HIT("dwarf_shield_hit_", "shield_damage", 0f, true, false, true, 0f); // dwarf holding a shield (and gets hit)
	
	private final String animationPrefix;
	private final String sound;
	private final float enduranceCosts;
	private final boolean animated;
	private final boolean interruptable;
	private final boolean moveBlocking;
	private final float damage;
	
	private static final String defaultAnimationDirection = "right";
	
	public static boolean isAttack(CharacterAction action) {
		return action == ATTACK || action == ATTACK_JUMP || action == ATTACK_SPIN;
	}
	
	private CharacterAction(String animationPrefix, String sound, float enduranceCosts, boolean animated, boolean interruptable, boolean moveBlocking,
			float damage) {
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
}
