package net.jfabricationgames.gdx.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.jfabricationgames.gdx.Game;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox.TextureType;
import net.jfabricationgames.gdx.texture.TextureLoader;

public class LoadingScreen extends MenuScreen<LoadingScreen> {
	
	private static final String ASSET_GROUP_NAME_LOADING_SCREEN = "loading_screen";
	private static final String TEXTURE_CONFIG = "config/menu/loading_screen/loading_screen_textures.json";
	
	private MenuBox background;
	private MenuBox banner;
	
	private AssetGroupManager assetManager;
	private Runnable afterFinishedLoading;
	
	private AnimationDirector<TextureRegion> dwarfAnimation;
	private TextureRegion chestTexture;
	
	public LoadingScreen(Runnable afterFinishedLoading) {
		this.afterFinishedLoading = afterFinishedLoading;
		assetManager = AssetGroupManager.getInstance();
		dwarfAnimation = AnimationManager.getInstance().getTextureAnimationDirector("dwarf_run_right");
		
		TextureLoader textureLoader = new TextureLoader(TEXTURE_CONFIG);
		chestTexture = textureLoader.loadTexture("chest");
		
		createComponents();
	}
	
	private void createComponents() {
		background = new MenuBox(10, 2, TextureType.YELLOW_BOARD);
		banner = new MenuBox(5, 2, TextureType.BIG_BANNER);
	}
	
	@Override
	public void showMenu() {
		Game game = Game.getInstance();
		game.setScreen(this);
	}
	
	@Override
	protected String getAssetGroupName() {
		return ASSET_GROUP_NAME_LOADING_SCREEN;
	}
	
	@Override
	protected String getInputContextName() {
		return null;
	}
	
	@Override
	protected void setFocusTo(String stateName, String leavingState) {}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		dwarfAnimation.increaseStateTime(delta);
		
		batch.begin();
		drawBackground();
		drawBanner();
		drawLoadingBar();
		batch.end();
		
		drawTexts();
		
		if (assetManager.finishedLoading()) {
			afterFinishedLoading.run();
		}
	}
	
	private void drawBackground() {
		background.draw(batch, 300, 200, 580, 100);
	}
	
	private void drawBanner() {
		banner.draw(batch, 280, 250, 600, 250);
	}
	
	private void drawLoadingBar() {
		float progress = assetManager.getProgress();
		float positionX = 315f + 420f * progress;
		float positionY = 220f;
		
		batch.draw(chestTexture, 790, 210, 80, 80);
		
		TextureRegion dwarf = dwarfAnimation.getKeyFrame();
		batch.draw(dwarf, positionX, positionY, 80, 80);
	}
	
	private void drawTexts() {
		screenTextWriter.setColor(Color.RED);
		screenTextWriter.setScale(2f);
		screenTextWriter.drawText("Dwarf Scroller GDX", 100, 700);
		
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.5f);
		screenTextWriter.drawText("Loading...", 430, 393);
	}
}
