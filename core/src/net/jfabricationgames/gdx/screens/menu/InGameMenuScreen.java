package net.jfabricationgames.gdx.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.screens.menu.control.ControlledMenu;
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public abstract class InGameMenuScreen<T extends ControlledMenu<T>> extends ControlledMenu<T> {
	
	public static final String TEXT_COLOR_ENCODING_NORMAL = "[#000000]";
	public static final String TEXT_COLOR_ENCODING_FOCUS = "[#C8441B]";
	public static final int VIRTUAL_WIDTH = 1280;
	public static final int VIRTUAL_HEIGHT = 837;//820 seems to be smaller here than in the GameScreen...
	
	public static final String ASSET_GROUP_NAME = "main_menu";
	public static final String FONT_NAME = "vikingMedium";
	
	protected GameScreen gameScreen;
	protected FrameBuffer gameSnapshotFrameBuffer;
	protected Sprite gameSnapshotSprite;
	protected AssetGroupManager assetManager;
	protected ScreenTextWriter screenTextWriter;
	protected Viewport viewport;
	protected SpriteBatch batch;
	protected PlayableCharacter player;
	
	public InGameMenuScreen(String statesConfig, GameScreen gameScreen, PlayableCharacter player) {
		super(statesConfig);
		this.gameScreen = gameScreen;
		this.player = player;
		
		gameSnapshotFrameBuffer = new FrameBuffer(Format.RGB888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(FONT_NAME);
		
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		batch = new SpriteBatch();
		
		assetManager = AssetGroupManager.getInstance();
		assetManager.loadGroup(ASSET_GROUP_NAME);
		assetManager.finishLoading();
	}

	@Override
	public void showMenu() {
		DwarfScrollerGame game = DwarfScrollerGame.getInstance();
		game.changeInputContext(getInputContextName());
		game.getInputContext().addListener(this);
		game.setScreen(this);
	}
	
	public void backToGame() {
		Gdx.app.debug(getClass().getSimpleName(), "'Back To Game' selected");
		removeInputListener();
		DwarfScrollerGame game = DwarfScrollerGame.getInstance();
		game.changeInputContext(GameScreen.INPUT_CONTEXT_NAME);
		game.setScreen(gameScreen);
	}
	
	protected void removeInputListener() {
		DwarfScrollerGame.getInstance().getInputContext().removeListener(this);
	}
	
	protected abstract String getInputContextName();

	public void restartGame() {
		Gdx.app.debug(getClass().getSimpleName(), "'Restart Game' selected");
		gameScreen.dispose();
		DwarfScrollerGame.getInstance().setScreen(new GameScreen());
	}
	
	public void respawnInLastCheckpoint() {
		Gdx.app.debug(getClass().getSimpleName(), "'Respawn' selected");
		player.respawn();
		backToGame();
		gameScreen.setGameOver(false);
	}
	
	public void quitGame() {
		Gdx.app.debug(getClass().getSimpleName(), "'Quit Game' selected");
		Gdx.app.exit();
	}
	
	protected void takeGameSnapshot() {
		gameSnapshotFrameBuffer.bind();
		gameScreen.render(0f);
		FrameBuffer.unbind();
		
		Texture gameSnapshotTexture = gameSnapshotFrameBuffer.getColorBufferTexture();
		gameSnapshotSprite = new Sprite(gameSnapshotTexture);
		gameSnapshotSprite.flip(false, true);
		gameSnapshotSprite.setColor(Color.GRAY);
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void dispose() {
		assetManager.unloadGroup(ASSET_GROUP_NAME);
		gameSnapshotFrameBuffer.dispose();
	}
}
