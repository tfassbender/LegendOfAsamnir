package net.jfabricationgames.gdx.character.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.jfabricationgames.gdx.attack.hit.Hittable;
import net.jfabricationgames.gdx.character.player.implementation.SpecialAction;
import net.jfabricationgames.gdx.cutscene.action.CutsceneMoveableUnit;
import net.jfabricationgames.gdx.hud.StatsCharacter;
import net.jfabricationgames.gdx.object.InteractivePlayer;

public interface PlayableCharacter extends StatsCharacter, CutsceneMoveableUnit, Hittable, InteractivePlayer {
	
	public void process(float delta);
	public void render(float delta, SpriteBatch batch);
	public void renderDarkness(SpriteBatch batch, ShapeRenderer shapeRenderer);
	
	public boolean isAlive();
	public void removeFromMap();
	public void respawn();
	public void reAddToWorld();
	
	public void setPosition(float x, float y);
	
	public SpecialAction getActiveSpecialAction();
	public void setActiveSpecialAction(SpecialAction specialAction);
	
	public int getCoins();
	
	public void centerCameraOnPlayer();
}
