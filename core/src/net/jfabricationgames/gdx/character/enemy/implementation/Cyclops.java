package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.ai.BaseAI;
import net.jfabricationgames.gdx.character.ai.util.timer.FixedAttackTimer;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.character.enemy.ai.FightAI;
import net.jfabricationgames.gdx.data.GameDataService;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;

public class Cyclops extends Enemy implements EventListener {
	
	public Cyclops(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		//TODO only take damage in certain states
		super.takeDamage(damage, attackType);
	}
	
	@Override
	protected void createAI() {
		ai = new BaseAI();
		ai = new FightAI(ai, stateMachine.getState("attack_throw"), new FixedAttackTimer(5f), 5f);
	}
	

	@Override
	protected String getDamageStateName(float damage) {
		if (damage >= 15) {
			return "damage_high";
		}
		else {
			return "damage_low";
		}
	}
	
	@Override
	protected void die() {
		super.die();
		GameDataService.fireQuickSaveEvent();
	}

	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.PLAYER_RESPAWNED) {
			resetHealthToMaximum();
		}
	}
	
	private void resetHealthToMaximum() {
		health = typeConfig.health;
	}
	
	@Override
	public void removeFromMap() {
		super.removeFromMap();
	}
}
