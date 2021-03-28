package net.jfabricationgames.gdx.attack;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.physics.PhysicsCollisionType;

public class AttackCreator {
	
	private Body body;
	private PhysicsCollisionType collisionType;
	
	private Array<Attack> attacks;
	private ArrayMap<String, AttackConfig> configs;
	
	public AttackCreator(String attackConfigFile, Body body, PhysicsCollisionType collisionType) {
		this.body = body;
		this.collisionType = collisionType;
		attacks = new Array<>();
		
		loadAttackConfig(attackConfigFile);
	}
	
	private void loadAttackConfig(String attackConfigFile) {
		configs = new ArrayMap<>();
		FileHandle attackConfigFileHandle = Gdx.files.internal(attackConfigFile);
		
		Json json = new Json();
		@SuppressWarnings("unchecked")
		Array<AttackConfig> attackConfigs = json.fromJson(Array.class, AttackConfig.class, attackConfigFileHandle);
		
		for (AttackConfig config : attackConfigs) {
			configs.put(config.id, config);
		}
	}
	
	public void handleAttacks(float delta) {
		Iterator<Attack> iter = attacks.iterator();
		while (iter.hasNext()) {
			Attack attack = iter.next();
			
			attack.increaseTimer(delta);
			
			if (attack.isExecuted()) {
				attack.remove();
				iter.remove();
			}
			
			if (attack.isToStart()) {
				attack.start();
			}
		}
	}
	
	public void renderAttacks(float delta, SpriteBatch batch) {
		for (Attack attack : attacks) {
			attack.render(delta, batch);
		}
	}
	
	public boolean allAttacksExecuted() {
		for (Attack attack : attacks) {
			if (!attack.isExecuted()) {
				return false;
			}
		}
		return true;
	}
	
	public Attack startAttack(String attack, Vector2 direction) {
		return startAttack(attack, direction, collisionType);
	}
	public Attack startAttack(String attack, Vector2 direction, PhysicsCollisionType collisionType) {
		return startAttack(configs.get(attack), direction, collisionType);
	}
	
	private Attack startAttack(AttackConfig config, Vector2 direction, PhysicsCollisionType collisionType) {
		Attack attack = AttackFactory.createAttack(config, direction, body, collisionType);
		attacks.add(attack);
		return attack;
	}
	
	/**
	 * Set the body that gets the hit fixtures, because the body might not have been created when the AttackCreator was initialized.
	 */
	public void setBody(Body body) {
		this.body = body;
	}
	
	public void handleAttackDamage(Contact contact) {
		for (Attack attack : attacks) {
			attack.dealAttackDamage(contact);
		}
	}
}
