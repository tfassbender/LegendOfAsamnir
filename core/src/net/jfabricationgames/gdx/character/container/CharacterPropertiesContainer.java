package net.jfabricationgames.gdx.character.container;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.character.CharacterAction;
import net.jfabricationgames.gdx.character.container.data.CharacterProperties;

public class CharacterPropertiesContainer {
	
	private CharacterProperties properties;
	
	public CharacterPropertiesContainer() {
		properties = new CharacterProperties();
	}
	public void updateStats(float delta, CharacterAction action) {
		//recharge endurance by time
		properties.endurance = Math.min(properties.endurance + delta * getEnduranceCharge(action), properties.maxEndurance);
		
		//increase health, mana and endurance
		if (properties.increaseHealth > 0f) {
			float increaseStep = Math.min(delta * properties.healthIncreasePerSecond, properties.increaseHealth);
			properties.increaseHealth -= increaseStep;
			properties.health = Math.min(properties.health + increaseStep, properties.maxHealth);
		}
		if (properties.increaseMana > 0f) {
			float increaseStep = Math.min(delta * properties.manaIncreasePerSecond, properties.increaseMana);
			properties.increaseMana -= increaseStep;
			properties.mana = Math.min(properties.mana + increaseStep, properties.maxMana);
		}
		if (properties.increaseEndurance > 0f) {
			float increaseStep = Math.min(delta * properties.enduranceIncreasePerSecond, properties.increaseEndurance);
			properties.increaseEndurance -= increaseStep;
			properties.endurance = Math.min(properties.endurance + increaseStep, properties.maxEndurance);
		}
		if (properties.increaseArmor > 0f) {
			float increaseStep = Math.min(delta * properties.armorIncreasePerSecond, properties.increaseArmor);
			properties.increaseArmor -= increaseStep;
			properties.armor = Math.min(properties.armor + increaseStep, properties.maxArmor);
		}
		if (properties.decreaseCoins > 0f) {
			float decreaseStep = Math.min(delta * properties.coinsDecreasePerSecond, properties.decreaseCoins);
			properties.decreaseCoins -= decreaseStep;
			properties.coins = Math.max(Math.min(properties.coins - decreaseStep, properties.maxCoins), 0);
			if (properties.coins == 0 && properties.decreaseCoins > 0) {
				properties.decreaseCoins = 0;
			}
		}
	}
	private float getEnduranceCharge(CharacterAction action) {
		if (action == CharacterAction.NONE || action == CharacterAction.IDLE) {
			return properties.enduranceChargeIdle;
		}
		else {
			return properties.enduranceChargeMoving;
		}
	}
	
	public boolean hasEnoughEndurance(CharacterAction action) {
		return properties.endurance >= action.getEnduranceCosts();
	}
	
	public void reduceEnduranceForAction(CharacterAction action) {
		properties.endurance = Math.max(0, properties.endurance - action.getEnduranceCosts());
	}
	
	public boolean hasBlock() {
		return properties.armor > 0;
	}
	
	public boolean isSlowedDown() {
		return properties.slowedDown;
	}
	public void setSlowedDown(boolean slowedDown) {
		properties.slowedDown = slowedDown;
	}
	
	public void reduceEnduranceForSprinting(float delta) {
		properties.endurance -= properties.enduranceCostsSprint * delta;
		if (properties.endurance < 0) {
			properties.endurance = 0;
		}
	}
	public boolean isExhausted() {
		return properties.endurance < 1e-5;
	}
	
	public float getHealthPercentual() {
		return properties.health / properties.maxHealth;
	}
	
	public void takeDamage(float damage) {
		properties.health -= damage;
		if (properties.health < 0) {
			properties.health = 0;
		}
	}
	
	public boolean isAlive() {
		return properties.health > 0;
	}
	
	public void increaseHealth(float amount) {
		properties.increaseHealth += amount;
	}
	
	public float getManaPercentual() {
		return properties.mana / properties.maxMana;
	}
	
	public void increaseMana(float amount) {
		properties.increaseMana += amount;
	}
	
	public float getEndurancePercentual() {
		return properties.endurance / properties.maxEndurance;
	}
	
	public void increaseArmor(float amount) {
		properties.increaseArmor += amount;
	}
	
	public float getArmorPercentual() {
		return properties.armor / properties.maxArmor;
	}
	
	public void takeArmorDamage(float damage) {
		properties.armor = Math.max(properties.armor - damage, 0);
	}
	
	public void increaseCoins(int coins) {
		properties.coins += coins;
	}
	public void reduceCoins(int coins) {
		properties.decreaseCoins += coins;
	}
	/**
	 * Shows the decrease of coins if properties.decreaseCoins is greater than 0.
	 */
	public int getCoinsForHud() {
		return (int) Math.round(properties.coins);
	}
	public int getCoins() {
		return (int) Math.round(properties.coins - properties.decreaseCoins);
	}
	
	public void changeStatsAfterRespawn() {
		properties.health = 0f;
		properties.increaseHealth = properties.maxHealth;
		properties.armor = 0f;
		properties.increaseArmor = properties.maxArmor * 0.5f;
		properties.mana = 0f;
		properties.increaseMana = properties.maxMana;
		properties.endurance = 0f;
		reduceCoins(50);
	}
	
	public Vector2 getRespawnPoint() {
		return properties.respawnPoint;
	}
	public void setRespawnPoint(Vector2 respawnPoint) {
		properties.respawnPoint = respawnPoint;
	}
}
