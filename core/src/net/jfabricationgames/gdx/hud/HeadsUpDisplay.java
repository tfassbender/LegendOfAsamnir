package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.character.player.Player;

public class HeadsUpDisplay implements Disposable {
	
	private final float hudSceneWidth;
	private final float hudSceneHeight;
	private OrthographicCamera camera;
	private StatsCharacter character;
	
	private StatusBar statusBar;
	private OnScreenItemRenderer onScreenItemRenderer;
	private OnScreenRuneRenderer onScreenRuneRenderer;
	private OnScreenTextBox onScreenText;
	private WorldEdge worldEdge;
	
	public HeadsUpDisplay(float hudSceneWidth, float hudSceneHeight, OrthographicCamera camera) {
		this.hudSceneWidth = hudSceneWidth;
		this.hudSceneHeight = hudSceneHeight;
		this.camera = camera;
		
		character = Player.getInstance();
		
		statusBar = new StatusBar(camera, character, hudSceneWidth, hudSceneHeight);
		onScreenItemRenderer = new OnScreenItemRenderer(camera, character, hudSceneWidth, hudSceneHeight);
		onScreenRuneRenderer = new OnScreenRuneRenderer(camera, hudSceneWidth, hudSceneHeight);
		onScreenText = OnScreenTextBox.createInstance(camera, hudSceneWidth, hudSceneHeight);
		worldEdge = new WorldEdge(camera);
	}
	
	public void render(float delta) {
		statusBar.render(delta);
		onScreenItemRenderer.render(delta);
		onScreenRuneRenderer.render(delta);
		onScreenText.render(delta);
		worldEdge.render(delta);
	}
	
	@Override
	public void dispose() {
		statusBar.dispose();
		onScreenItemRenderer.dispose();
		onScreenText.dispose();
		worldEdge.dispose();
	}
	
	protected float getHudSceneWidth() {
		return hudSceneWidth;
	}
	
	protected float getHudSceneHeight() {
		return hudSceneHeight;
	}
	
	protected OrthographicCamera getCamera() {
		return camera;
	}
	
	protected StatsCharacter getCharacter() {
		return character;
	}
}
