package net.jfabricationgames.gdx.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.screens.menu.components.MenuBackground;

public class InGameMenuScreen extends ScreenAdapter implements InputActionListener {
	
	public static final int VIRTUAL_WIDTH = 1280;
	public static final int VIRTUAL_HEIGHT = 837;//820 seems to be smaller here than in the GameScreen...
	
	public static final String ASSET_GROUP_NAME = "main_menu";
	public static final String INPUT_CONTEXT_NAME = "inGameMenu";
	
	public static final String ACTION_BACK_TO_GAME = "backToGame";
	
	private Viewport viewport;
	
	private AssetGroupManager assetManager;
	private GameScreen gameScreen;
	
	private SpriteBatch batch;
	private FrameBuffer gameSnapshotFrameBuffer;
	private Sprite gameSnapshotSprite;
	
	private MenuBackground background;
	
	public InGameMenuScreen(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
		
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		gameSnapshotFrameBuffer = new FrameBuffer(Format.RGB888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);
		batch = new SpriteBatch();
		
		assetManager = AssetGroupManager.getInstance();
		assetManager.loadGroup(ASSET_GROUP_NAME);
		assetManager.finishLoading();
		
		background = new MenuBackground(12, 8, MenuBackground.TextureType.GREEN_BOARD);
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (action.equals(ACTION_BACK_TO_GAME) && (type == Type.KEY_DOWN || type == Type.CONTROLLER_BUTTON_PRESSED)) {
			backToGame();
			return true;
		}
		return false;
	}
	
	@Override
	public Priority getInputPriority() {
		return Priority.MENU;
	}
	
	public void showMenu() {
		DwarfScrollerGame game = DwarfScrollerGame.getInstance();
		game.changeInputContext(INPUT_CONTEXT_NAME);
		game.getInputContext().addListener(this);
		game.setScreen(this);
		
		takeGameSnapshot();
	}
	
	private void takeGameSnapshot() {
		gameSnapshotFrameBuffer.bind();
		gameScreen.render(0f);
		FrameBuffer.unbind();
		
		Texture gameSnapshotTexture = gameSnapshotFrameBuffer.getColorBufferTexture();
		gameSnapshotSprite = new Sprite(gameSnapshotTexture);
		gameSnapshotSprite.flip(false, true);
		gameSnapshotSprite.setColor(Color.GRAY);
	}
	
	private void backToGame() {
		DwarfScrollerGame game = DwarfScrollerGame.getInstance();
		game.getInputContext().removeListener(this);
		game.changeInputContext(GameScreen.INPUT_CONTEXT_NAME);
		game.setScreen(gameScreen);
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		gameSnapshotSprite.draw(batch);
		background.draw(batch, 100, 100, VIRTUAL_WIDTH - 200, VIRTUAL_HEIGHT - 200);
		batch.end();
	}
	
	@Override
	public void dispose() {
		assetManager.unloadGroup(ASSET_GROUP_NAME);
		DwarfScrollerGame.getInstance().getInputContext().removeListener(this);
		
		gameSnapshotFrameBuffer.dispose();
	}
}
