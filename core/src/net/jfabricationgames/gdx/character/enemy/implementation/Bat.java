package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;

public class Bat extends Enemy {
	
	public Bat(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected String getIdleStateName() {
		return getMovingStateName();
	}
	
	@Override
	protected String getDamageStateName(float damage) {
		return getMovingStateName();
	}
}
