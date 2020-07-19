package net.jfabricationgames.gdx.character;

import net.jfabricationgames.gdx.character.animation.MovingDirection;

public enum CharacterAction {
	
	NONE("", false, true, false),// just stay still
	IDLE("dwarf_idle_", true, true, false), // staying still for some time
	RUN("dwarf_run_", true, true, false), // running in any direction
	JUMP("dwarf_jump_", true, false, true), // jumping with or without direction
	ATTACK("dwarf_attack_", true, false, true), // attack while standing 
	ATTACK_JUMP("dwarf_attack_jump_", true, false, true), // attack while running
	ATTACK_SPIN("dwarf_spin_", true, false, true), // spin attack
	HIT("dwarf_hit_", true, false, false), // dwarf got hit
	DIE("dwarf_die_", true, false, true); // dwarf died
	
	private final String animationPrefix;
	private final boolean animated;
	private final boolean interruptable;
	private final boolean moveBlocking;
	
	private CharacterAction(String animationPrefix, boolean animated, boolean interruptable, boolean moveBlocking) {
		if (interruptable && moveBlocking) {
			throw new IllegalArgumentException("An action can't be interruptable and move blocking at the same time");
		}
		
		this.animationPrefix = animationPrefix;
		this.animated = animated;
		this.interruptable = interruptable;
		this.moveBlocking = moveBlocking;
	}
	
	public String getAnimationName(MovingDirection direction) {
		return animationPrefix + direction.getAnimationDirectionPostfix();
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
