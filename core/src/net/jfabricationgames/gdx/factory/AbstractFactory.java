package net.jfabricationgames.gdx.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.animation.AnimationFrame;
import net.jfabricationgames.gdx.map.GameMap;
import net.jfabricationgames.gdx.screens.game.GameScreen;

public abstract class AbstractFactory {
	
	protected Json json;
	protected TextureAtlas atlas;
	protected World world;
	protected GameMap gameMap;
	
	public AbstractFactory() {
		json = new Json();
	}
	
	public <T> T loadConfig(Class<T> clazz, String configFile) {
		return json.fromJson(clazz, Gdx.files.internal(configFile));
	}
	
	protected Sprite createSprite(float x, float y, String textureName) {
		AnimationFrame animationFrame = AnimationFrame.getAnimationFrame(textureName);
		TextureRegion textureRegion = animationFrame.findRegion(atlas);
		
		Sprite sprite = new Sprite(textureRegion);
		sprite.setX(x * GameScreen.WORLD_TO_SCREEN - sprite.getWidth() * 0.5f);
		sprite.setY(y * GameScreen.WORLD_TO_SCREEN - sprite.getHeight() * 0.5f);
		sprite.setScale(GameScreen.WORLD_TO_SCREEN);
		return sprite;
	}
}
