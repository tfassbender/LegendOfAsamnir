package net.jfabricationgames.gdx.map;

import net.jfabricationgames.gdx.character.animal.Animal;
import net.jfabricationgames.gdx.character.enemy.Enemy;
import net.jfabricationgames.gdx.character.npc.NonPlayableCharacter;
import net.jfabricationgames.gdx.cutscene.CutsceneHandler;
import net.jfabricationgames.gdx.projectile.Projectile;

class GameMapProcessor {
	
	private GameMap gameMap;
	
	private CutsceneHandler cutsceneHandler;
	
	public GameMapProcessor(GameMap gameMap) {
		this.gameMap = gameMap;
		
		cutsceneHandler = CutsceneHandler.getInstance();
	}
	
	public void processCutscene(float delta) {
		cutsceneHandler.act(delta);
	}
	
	public void processEnemies(float delta) {
		for (Enemy enemy : gameMap.enemies) {
			enemy.act(delta);
		}
	}
	
	public void processNpcs(float delta) {
		for (NonPlayableCharacter npc : gameMap.nonPlayableCharacters) {
			npc.act(delta);
		}
	}
	
	public void processAnimals(float delta) {
		for (Animal animal : gameMap.animals) {
			animal.act(delta);
		}
	}
	
	public void processProjectiles(float delta) {
		for (Projectile projectile : gameMap.projectiles) {
			projectile.update(delta);
		}
	}
}
