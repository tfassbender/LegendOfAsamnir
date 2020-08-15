package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Disposable;

public class HeadsUpDisplay implements Disposable {
	
	private final float hudSceneWidth;
	private final float hudSceneHeight;
	private OrthographicCamera camera;
	private StatsCharacter character;
	
	private StatusBar statusBar;
	private InGameMenu inGameMenu;
	private WorldEdge worldEdge;
	
	public HeadsUpDisplay(float hudSceneWidht, float hudSceneHeight, OrthographicCamera camera, StatsCharacter character) {
		this.hudSceneWidth = hudSceneWidht;
		this.hudSceneHeight = hudSceneHeight;
		this.camera = camera;
		this.character = character;
		
		statusBar = new StatusBar(this);
		inGameMenu = new InGameMenu(this);
		worldEdge = new WorldEdge(this);
	}
	
	public void render(float delta) {
		statusBar.render(delta);
		inGameMenu.render(delta);
		worldEdge.render(delta);
	}
	
	@Override
	public void dispose() {
		statusBar.dispose();
		inGameMenu.dispose();
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
