package net.jfabricationgames.gdx.character.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.jfabricationgames.gdx.attack.Hittable;
import net.jfabricationgames.gdx.character.player.implementation.SpecialAction;
import net.jfabricationgames.gdx.cutscene.action.CutsceneMoveableUnit;
import net.jfabricationgames.gdx.hud.StatsCharacter;
import net.jfabricationgames.gdx.map.GameMapObject;

public interface PlayableCharacter extends StatsCharacter, GameMapObject, CutsceneMoveableUnit, Hittable {
	
	public void process(float delta);
	
	public void render(float delta, SpriteBatch batch);
	
	public void renderDarkness(SpriteBatch batch, ShapeRenderer shapeRenderer);
	
	public boolean isAlive();
	
	public void respawn();
	
	public void reAddToWorld();
	
	public void setPosition(float x, float y);
	
	public void setActiveSpecialAction(SpecialAction specialAction);
	
	public int getCoins();
}
