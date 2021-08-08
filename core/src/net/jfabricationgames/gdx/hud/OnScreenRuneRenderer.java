package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.GrowingAnimationDirector;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventListener;
import net.jfabricationgames.gdx.event.EventType;

public class OnScreenRuneRenderer implements Disposable, EventListener {
	
	private static final String ANIMATION_RUNE_CONFIG_FILE = "config/animation/rune.json";
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private AnimationManager animationManager;
	private GrowingAnimationDirector<TextureRegion> runeAnimation;
	
	private Vector2 screenCenter;
	
	public OnScreenRuneRenderer(HeadsUpDisplay hud) {
		this.camera = hud.getCamera();
		batch = new SpriteBatch();
		
		screenCenter = new Vector2(hud.getHudSceneWidth() * 0.5f, hud.getHudSceneHeight() * 0.5f);
		
		animationManager = AnimationManager.getInstance();
		animationManager.loadAnimations(ANIMATION_RUNE_CONFIG_FILE);
		
		EventHandler.getInstance().registerEventListener(this);
	}
	
	public void render(float delta) {
		if (runeAnimation != null) {
			runeAnimation.increaseStateTime(delta);
			
			batch.setProjectionMatrix(camera.combined);
			drawRunes();
			
			if (runeAnimation.isAnimationFinished()) {
				runeAnimation = null;
			}
		}
	}
	
	private void drawRunes() {
		batch.begin();
		batch.draw(runeAnimation.getKeyFrame(), //
				screenCenter.x - runeAnimation.getWidth() * 0.5f, //
				screenCenter.y - runeAnimation.getHeight() * 0.5f, //
				runeAnimation.getWidth(), //
				runeAnimation.getHeight());
		batch.end();
	}
	
	@Override
	public void handleEvent(EventConfig event) {
		if (event.eventType == EventType.RUNE_USED) {
			runeAnimation = animationManager.getGrowingAnimationDirector(event.stringValue);
		}
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		EventHandler.getInstance().removeEventListener(this);
	}
}
