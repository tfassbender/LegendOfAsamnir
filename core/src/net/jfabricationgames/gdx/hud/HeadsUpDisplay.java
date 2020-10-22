package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Disposable;

public class HeadsUpDisplay implements Disposable {
	
	public static final String DEFAULT_FONT_NAME = "vikingMedium";
	
	private final float hudSceneWidth;
	private final float hudSceneHeight;
	private OrthographicCamera camera;
	private StatsCharacter character;
	
	private StatusBar statusBar;
	private OnScreenItemRenderer onScreenItemRenderer;
	private InGameMenu inGameMenu;
	private OnScreenTextBox onScreenText;
	private WorldEdge worldEdge;
	
	public HeadsUpDisplay(float hudSceneWidht, float hudSceneHeight, OrthographicCamera camera, StatsCharacter character) {
		this.hudSceneWidth = hudSceneWidht;
		this.hudSceneHeight = hudSceneHeight;
		this.camera = camera;
		this.character = character;
		
		statusBar = new StatusBar(this);
		onScreenItemRenderer = new OnScreenItemRenderer(this);
		inGameMenu = new InGameMenu(this);
		onScreenText = OnScreenTextBox.createInstance(this);
		worldEdge = new WorldEdge(this);
	}
	
	public void render(float delta) {
		statusBar.render(delta);
		onScreenItemRenderer.render(delta);
		inGameMenu.render(delta);
		onScreenText.render(delta);
		worldEdge.render(delta);
	}
	
	@Override
	public void dispose() {
		statusBar.dispose();
		onScreenItemRenderer.dispose();
		inGameMenu.dispose();
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
