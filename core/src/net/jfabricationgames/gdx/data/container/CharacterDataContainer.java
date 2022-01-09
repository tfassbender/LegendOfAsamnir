package net.jfabricationgames.gdx.data.container;

import com.badlogic.gdx.math.Vector2;

public class CharacterDataContainer {
	
	public boolean slowedDown;
	
	public float health = 100f;
	public float maxHealth = 100f;
	public float increaseHealth = 0f;
	public final float healthIncreasePerSecond = 25f;
	
	public float mana = 100f;
	public float maxMana = 100f;
	public float increaseMana = 0f;
	public float manaIncreasePerSecond = 25f;
	
	public float endurance = 100f;
	public float maxEndurance = 100f;
	public float increaseEndurance = 0f;
	public float enduranceIncreasePerSecond = 25f;
	
	public float enduranceCostsSprint = 15f;
	public float enduranceCostsBlock = 5f;
	public float enduranceCostHitBlocking = 10f;
	
	public float armor = 50f;
	public float maxArmor = 100f;
	public float increaseArmor = 0f;
	public final float armorIncreasePerSecond = 25f;
	
	public float coins;
	public float maxCoins = 999;
	public float decreaseCoins;
	public final float coinsDecreasePerSecond = 20f;
	
	public Vector2 respawnPoint;
	public Vector2 position;
}
