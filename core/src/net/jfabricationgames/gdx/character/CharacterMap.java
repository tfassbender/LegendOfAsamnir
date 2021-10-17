package net.jfabricationgames.gdx.character;

import com.badlogic.gdx.physics.box2d.Body;

import net.jfabricationgames.gdx.character.animal.Animal;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.npc.NonPlayableCharacter;

public interface CharacterMap {
	
	public void addEnemy(Enemy gameObject);
	
	public void removeEnemy(Enemy enemy, Body body);
	public void removeAnimal(Animal animal, Body body);
	public void removeNpc(NonPlayableCharacter nonPlayableCharacter, Body body);
}
