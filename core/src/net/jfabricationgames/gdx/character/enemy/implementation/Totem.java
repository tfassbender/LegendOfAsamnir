package net.jfabricationgames.gdx.character.enemy.implementation;

import com.badlogic.gdx.maps.MapProperties;

import net.jfabricationgames.gdx.attack.AttackType;
import net.jfabricationgames.gdx.character.AbstractCharacter;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.enemy.EnemyTypeConfig;
import net.jfabricationgames.gdx.physics.PhysicsBodyCreator.PhysicsBodyProperties;

public class Totem extends Enemy {
	
	private static final float MAGIC_ATTACK_DAMAGE = 5f;

	public Totem(EnemyTypeConfig typeConfig, MapProperties properties) {
		super(typeConfig, properties);
	}
	
	@Override
	protected PhysicsBodyProperties definePhysicsBodyProperties() {
		PhysicsBodyProperties properties = super.definePhysicsBodyProperties();
		properties.setDensity(AbstractCharacter.DENSITY_IMMOVABLE);
		return properties;
	}
	
	@Override
	protected String getMovingStateName() {
		return getIdleStateName();
	}
	
	@Override
	public void takeDamage(float damage, AttackType attackType) {
		if (attackType.isSubTypeOf(AttackType.MAGIC)) {
			//let magic attacks always cause the same damage (otherwise the WAND attack would cause no damage and no push force)
			super.takeDamage(MAGIC_ATTACK_DAMAGE, attackType);
		}
	}
}
