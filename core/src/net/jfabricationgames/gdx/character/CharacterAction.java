package net.jfabricationgames.gdx.character;

public enum CharacterAction {
	
	NONE("", null, 0f, false, true, false),// just stay still
	IDLE("dwarf_idle_", null, 0f, true, true, false), // staying still for some time
	RUN("dwarf_run_", null, 0f, true, true, false), // running in any direction
	JUMP("dwarf_jump_", "jump", 10f, true, false, true), // jumping with or without direction
	ATTACK("dwarf_attack_", "hit", 10f, true, false, false), // attack while standing 
	ATTACK_JUMP("dwarf_attack_jump_", "hit2", 20f, true, false, true), // attack while running
	ATTACK_SPIN("dwarf_spin_", "hit", 10f, true, false, true), // spin attack
	HIT("dwarf_hit_", "damage", 0f, true, false, false), // dwarf got hit
	DIE("dwarf_die_", "damage", 0f, true, false, true); // dwarf died
	
	private final String animationPrefix;
	private final String sound;
	private final float enduranceCosts;
	private final boolean animated;
	private final boolean interruptable;
	private final boolean moveBlocking;
	
	private static final String defaultAnimationDirection = "right";
	
	private CharacterAction(String animationPrefix, String sound, float enduranceCosts, boolean animated, boolean interruptable,
			boolean moveBlocking) {
		if (interruptable && moveBlocking) {
			throw new IllegalArgumentException("An action can't be interruptable and move blocking at the same time");
		}
		
		this.animationPrefix = animationPrefix;
		this.sound = sound;
		this.enduranceCosts = enduranceCosts;
		this.animated = animated;
		this.interruptable = interruptable;
		this.moveBlocking = moveBlocking;
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
}
