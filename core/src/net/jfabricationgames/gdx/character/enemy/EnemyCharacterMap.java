package net.jfabricationgames.gdx.character.enemy;

import com.badlogic.gdx.physics.box2d.Body;

public interface EnemyCharacterMap {
	
	public void addEnemy(Enemy gameObject);
	public void removeEnemy(Enemy enemy, Body body);
}
