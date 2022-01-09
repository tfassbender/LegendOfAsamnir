package net.jfabricationgames.gdx.data.handler;

import com.badlogic.gdx.math.Vector2;

import net.jfabricationgames.gdx.data.container.CharacterDataContainer;
import net.jfabricationgames.gdx.data.container.GameDataContainer;
import net.jfabricationgames.gdx.data.handler.type.DataCharacterAction;

public class CharacterPropertiesDataHandler implements DataHandler {
	
	private static CharacterPropertiesDataHandler instance;
	
	public static synchronized CharacterPropertiesDataHandler getInstance() {
		if (instance == null) {
			instance = new CharacterPropertiesDataHandler();
		}
		return instance;
	}
	
	private CharacterDataContainer properties;
	
	private CharacterPropertiesDataHandler() {}
	
	@Override
	public void updateData(GameDataContainer dataContainer) {
		properties = dataContainer.characterDataContainer;
	}
	
	public void updateStats(float delta, DataCharacterAction action) {
		//recharge endurance by time
		properties.endurance = Math.min(properties.endurance + delta * action.getEnduranceRecharge(), properties.maxEndurance);
		
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
			//only used when endurance is increased by an event or an item
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
	
	public boolean hasEnoughEndurance(DataCharacterAction action) {
		return hasEnoughEndurance(action.getEnduranceCosts());
	}
	public boolean hasEnoughEndurance(float endurance) {
		return properties.endurance >= endurance;
	}
	
	public void reduceEnduranceForAction(DataCharacterAction action) {
		reduceEndurance(action.getEnduranceCosts());
	}
	public void reduceEndurance(float endurance) {
		properties.endurance = Math.max(0, properties.endurance - endurance);
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
	public void reduceEnduranceForBlocking(float delta) {
		properties.endurance -= properties.enduranceCostsBlock * delta;
		if (properties.endurance < 0) {
			properties.endurance = 0;
		}
	}
	public void reduceEnduranceForHitBlocking() {
		properties.endurance -= properties.enduranceCostHitBlocking;
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
	
	public void increaseHealthByHalf() {
		properties.increaseHealth += properties.maxHealth * 0.5f;
	}
	
	public float getManaPercentual() {
		return properties.mana / properties.maxMana;
	}
	
	public boolean hasEnoughMana(float manaCost) {
		return properties.mana >= manaCost;
	}
	
	public void increaseMana(float amount) {
		properties.increaseMana += amount;
	}
	
	public void reduceMana(float amount) {
		properties.mana -= amount;
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
		return Math.round(properties.coins);
	}
	public int getCoins() {
		return Math.round(properties.coins - properties.decreaseCoins);
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
	
	public Vector2 getPlayerPosition() {
		return properties.position;
	}
	public void setPlayerPosition(Vector2 position) {
		properties.position = position;
	}
}
