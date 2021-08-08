package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.map.GameMap;

public class HeadsUpDisplay implements Disposable {
	
	public static final String DEFAULT_FONT_NAME = "vikingMedium";
	
	private final float hudSceneWidth;
	private final float hudSceneHeight;
	private OrthographicCamera camera;
	private StatsCharacter character;
	
	private StatusBar statusBar;
	private OnScreenItemRenderer onScreenItemRenderer;
	private OnScreenRuneRenderer onScreenRuneRenderer;
	private OnScreenTextBox onScreenText;
	private WorldEdge worldEdge;
	
	public HeadsUpDisplay(float hudSceneWidht, float hudSceneHeight, OrthographicCamera camera) {
		this.hudSceneWidth = hudSceneWidht;
		this.hudSceneHeight = hudSceneHeight;
		this.camera = camera;
		
		character = GameMap.getInstance().getPlayer();
		
		statusBar = new StatusBar(this);
		onScreenItemRenderer = new OnScreenItemRenderer(this);
		onScreenRuneRenderer = new OnScreenRuneRenderer(this);
		onScreenText = OnScreenTextBox.createInstance(this);
		worldEdge = new WorldEdge(this);
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
