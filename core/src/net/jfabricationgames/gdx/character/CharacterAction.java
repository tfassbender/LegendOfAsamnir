package net.jfabricationgames.gdx.character;

public enum CharacterAction {
	
	NONE("", null, false, true, false),// just stay still
	IDLE("dwarf_idle_", null, true, true, false), // staying still for some time
	RUN("dwarf_run_", null, true, true, false), // running in any direction
	JUMP("dwarf_jump_", "jump", true, false, true), // jumping with or without direction
	ATTACK("dwarf_attack_", "hit", true, false, true), // attack while standing 
	ATTACK_JUMP("dwarf_attack_jump_", "hit2", true, false, true), // attack while running
	ATTACK_SPIN("dwarf_spin_", "hit", true, false, true), // spin attack
	HIT("dwarf_hit_", "damage", true, false, false), // dwarf got hit
	DIE("dwarf_die_", "damage", true, false, true); // dwarf died
	
	private final String animationPrefix;
	private final String sound;
	private final boolean animated;
	private final boolean interruptable;
	private final boolean moveBlocking;
	
	private static final String defaultAnimationDirection = "right";
	
	private CharacterAction(String animationPrefix, String sound, boolean animated, boolean interruptable, boolean moveBlocking) {
		if (interruptable && moveBlocking) {
			throw new IllegalArgumentException("An action can't be interruptable and move blocking at the same time");
		}
		
		this.animationPrefix = animationPrefix;
		this.sound = sound;
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
