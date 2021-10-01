package net.jfabricationgames.gdx.constants;

public class Constants {
	
	public static final boolean DEBUG = false;
	
	public static final float WORLD_TO_SCREEN = 0.04f;
	public static final float SCREEN_TO_WORLD = 1f / WORLD_TO_SCREEN;
	public static final float SCENE_WIDTH = 12.80f;
	public static final float SCENE_HEIGHT = 8.20f;
	
	//the HUD uses a different scene size to make it easier to calculate in pixel units
	public static final float HUD_SCENE_FACTOR = 100f;
	public static final float HUD_SCENE_WIDTH = SCENE_WIDTH * HUD_SCENE_FACTOR;
	public static final float HUD_SCENE_HEIGHT = SCENE_HEIGHT * HUD_SCENE_FACTOR;
	
	private Constants() {}
}
