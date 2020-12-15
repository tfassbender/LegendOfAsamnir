package net.jfabricationgames.gdx.character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.container.CharacterFastTravelContainer;
import net.jfabricationgames.gdx.character.container.CharacterItemContainer;
import net.jfabricationgames.gdx.cutscene.action.CutsceneMoveableUnit;
import net.jfabricationgames.gdx.item.ItemAmmoType;

public interface PlayableCharacter extends CutsceneMoveableUnit {
	
	public void render(float delta, SpriteBatch batch);
	
	/**
	 * @param x
	 *        Movement in x direction (in world units).
	 * @param y
	 *        Movement in y direction (in world units).
	 */
	public void move(float x, float y);
	
	/**
	 * Get the current moving speed of the character, depending on the current action and the "sprint" state.
	 * 
	 * @return The current moving speed (per second) in world units.
	 */
	public float getMovingSpeed(boolean sprint);
	
	/**
	 * @return Returns true if the change was possible. False otherwise.
	 */
	public boolean changeAction(CharacterAction action);
	
	public CharacterAction getCurrentAction();
	
	/**
	 * @return True if the action was possible. False otherwise.
	 */
	public boolean executeSpecialAction();
	
	/**
	 * The time (in seconds) the character has to stay idle before an idle animation is started.
	 */
	public float getTimeTillIdleAnimation();
	
	/**
	 * The time (in seconds) the player needs to hold down the attack button, before a spin attack starts (when releasing the button).
	 */
	public float getHoldTimeTillSpinAttack();
	
	public void playSpinAttackChargedSound();
	
	public boolean isAnimationFinished();
	
	public void reduceEnduranceForSprinting(float delta);
	
	public boolean isExhausted();
	
	public boolean isAlive();
	
	public Vector2 getPosition();
	
	public SpecialAction getActiveSpecialAction();
	
	public void setActiveSpecialAction(SpecialAction specialAction);
	
	public boolean isGameOver();
	
	public int getAmmo(ItemAmmoType ammoType);

	public void setSlowedDown(boolean slowedDown);

	public void respawn();
	
	public int getCoins();

	public CharacterItemContainer getItemContainer();
	
	public CharacterFastTravelContainer getFastTravelContainer();
}
