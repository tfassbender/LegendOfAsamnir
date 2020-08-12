package net.jfabricationgames.gdx.character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface PlayableCharacter {

	/**
	 * Render the character.
	 * 
	 * @param delta The delta time since the last rendering step.
	 * @param batch The {@link SpriteBatch} on which the character is drawn.
	 */
	public void render(float delta, SpriteBatch batch);
	
	/**
	 * Move the character.
	 * 
	 * @param x Movement in x direction (in world units).
	 * @param y Movement in y direction (in world units).
	 */
	public void move(float x, float y);
	
	/**
	 * Get the current moving speed of the character, depending on the current action and the "sprint" state.
	 * 
	 * @param sprint True if the character should sprint. False otherwise.
	 * 
	 * @return The current moving speed (per second) in world units.
	 */
	public float getMovingSpeed(boolean sprint);
	
	/**
	 * Change the action of the character and return whether the change was possible.
	 * 
	 * @param action The new action to which the character shall change.
	 * 
	 * @return Returns true if the change was possible. False otherwise.
	 */
	public boolean changeAction(CharacterAction action);

	/**
	 * Get the characters current action.
	 * 
	 * @return The characters current action.
	 */
	public CharacterAction getCurrentAction();
	
	/**
	 * The time (in seconds) the character has to stay idle before an idle animation is started.
	 * 
	 * @return The time in seconds.
	 */
	public float getTimeTillIdleAnimation();

	/**
	 * Indicates whether the current animation is finished.
	 * 
	 * @return Returns true if the animation is finished. False if it's still playing.
	 */
	public boolean isAnimationFinished();
	
	/**
	 * Reduce the endurance of the character (by the endurance costs per second * delta), because he is sprinting.
	 * 
	 * @param delta The delta time since the last rendering step.
	 */
	public void reduceEnduranceForSprinting(float delta);
	
	/**
	 * Checks if the character is exhausted (endurance below or equal to zero).
	 * 
	 * @return True if the character is exhausted. False otherwise.
	 */
	public boolean isExhausted();
}
