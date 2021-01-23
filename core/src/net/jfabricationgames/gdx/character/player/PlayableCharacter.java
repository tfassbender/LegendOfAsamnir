package net.jfabricationgames.gdx.character.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.player.implementation.CharacterAction;
import net.jfabricationgames.gdx.character.player.implementation.SpecialAction;
import net.jfabricationgames.gdx.cutscene.action.CutsceneMoveableUnit;
import net.jfabricationgames.gdx.hud.StatsCharacter;
import net.jfabricationgames.gdx.item.ItemAmmoType;
import net.jfabricationgames.gdx.map.GameMapObject;

public interface PlayableCharacter extends StatsCharacter, GameMapObject, CutsceneMoveableUnit {
	
	public void render(float delta, SpriteBatch batch);
	
	public void move(float x, float y);
	
	/**
	 * Get the current moving speed of the character, depending on the current action and the "sprint" state.
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
	
	public void setPosition(float x, float y);
	
	public SpecialAction getActiveSpecialAction();
	
	public void setActiveSpecialAction(SpecialAction specialAction);
	
	public boolean isGameOver();
	
	public int getAmmo(ItemAmmoType ammoType);
	
	public void respawn();
	
	public int getCoins();
	
	public void reAddToWorld();
}
