package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class HeadsUpDisplay {
	
	private StatusBar statusBar;
	private InGameMenu inGameMenu;
	private WorldEdge worldEdge;
	
	public HeadsUpDisplay(OrthographicCamera camera) {
		statusBar = new StatusBar(camera);
		inGameMenu = new InGameMenu(camera);
		worldEdge = new WorldEdge(camera);
	}
	
	public void render(float delta) {
		statusBar.render(delta);
		inGameMenu.render(delta);
		worldEdge.render(delta);
	}
}
