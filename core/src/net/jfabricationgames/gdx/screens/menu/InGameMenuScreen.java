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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.character.SpecialAction;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.screens.menu.components.ItemMenu;
import net.jfabricationgames.gdx.screens.menu.components.MenuBackground;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public class InGameMenuScreen extends ScreenAdapter implements InputActionListener {

	public enum InputDirection {
		UP, DOWN, LEFT, RIGHT;
	}
	
	public static final int VIRTUAL_WIDTH = 1280;
	public static final int VIRTUAL_HEIGHT = 837;//820 seems to be smaller here than in the GameScreen...
	
	public static final String ASSET_GROUP_NAME = "main_menu";
	public static final String INPUT_CONTEXT_NAME = "inGameMenu";
	public static final String FONT_NAME = "vikingMedium";
	
	public static final String ACTION_BACK_TO_GAME = "backToGame";
	public static final String ACTION_SELECT = "select";
	public static final String ACTION_SELECTION_RIGHT = "right";
	public static final String ACTION_SELECTION_LEFT = "left";
	public static final String ACTION_SELECTION_DOWN = "down";
	public static final String ACTION_SELECTION_UP = "up";
	
	public static final int ITEM_MENU_ITEMS_PER_LINE = 4;
	public static final int ITEM_MENU_LINES = 2;
	
	private static final Array<String> items = new Array<String>(new String[] {"jump", "bow"});
	
	private Viewport viewport;
	
	private GameScreen gameScreen;
	private PlayableCharacter character;
	private AssetGroupManager assetManager;
	private ScreenTextWriter screenTextWriter;
	
	private SpriteBatch batch;
	private FrameBuffer gameSnapshotFrameBuffer;
	private Sprite gameSnapshotSprite;
	
	private MenuBox background;
	private MenuBox banner;
	private ItemMenu itemMenu;
	private MenuBox itemMenuBanner;
	
	private int selectedIndex;
	
	public InGameMenuScreen(GameScreen gameScreen, PlayableCharacter character) {
		this.gameScreen = gameScreen;
		this.character = character;
		
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		gameSnapshotFrameBuffer = new FrameBuffer(Format.RGB888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);
		batch = new SpriteBatch();
		
		assetManager = AssetGroupManager.getInstance();
		assetManager.loadGroup(ASSET_GROUP_NAME);
		assetManager.finishLoading();
		
		background = new MenuBackground(12, 8, MenuBox.TextureType.GREEN_BOARD);
		banner = new MenuBackground(6, 2, MenuBox.TextureType.BIG_BANNER);
		itemMenu = new ItemMenu(ITEM_MENU_ITEMS_PER_LINE, ITEM_MENU_LINES, items);
		itemMenuBanner = new MenuBackground(4, 2, MenuBox.TextureType.BIG_BANNER);
		
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(FONT_NAME);
		
		SpecialAction activeSpecialAction = character.getActiveSpecialAction();
		selectedIndex = activeSpecialAction != null ? activeSpecialAction.indexInMenu : 0;
		itemMenu.setHoveredIndex(selectedIndex);
		itemMenu.selectHoveredItem();
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (action.equals(ACTION_BACK_TO_GAME) && isEventTypeHandled(type)) {
			backToGame();
			return true;
		}
		if (action.equals(ACTION_SELECTION_UP) && isEventTypeHandled(type)) {
			changeSelection(InputDirection.UP);
		}
		if (action.equals(ACTION_SELECTION_DOWN) && isEventTypeHandled(type)) {
			changeSelection(InputDirection.DOWN);
		}
		if (action.equals(ACTION_SELECTION_LEFT) && isEventTypeHandled(type)) {
			changeSelection(InputDirection.LEFT);
		}
		if (action.equals(ACTION_SELECTION_RIGHT) && isEventTypeHandled(type)) {
			changeSelection(InputDirection.RIGHT);
		}
		if (action.equals(ACTION_SELECT) && isEventTypeHandled(type)) {
			selectCurrentIndex();
		}
		return false;
	}
	
	private void changeSelection(InputDirection direction) {
		//only item menu control at the moment
		switch (direction) {
			case UP:
				selectedIndex -= ITEM_MENU_ITEMS_PER_LINE;
				break;
			case DOWN:
				selectedIndex += ITEM_MENU_ITEMS_PER_LINE;
				break;
			case LEFT:
				selectedIndex--;
				break;
			case RIGHT:
				selectedIndex++;
				break;
			default:
				throw new IllegalStateException("Unexpected InputDirection: " + direction);
		}
		selectedIndex = Math.min(Math.max(selectedIndex, 0), ITEM_MENU_ITEMS_PER_LINE * ITEM_MENU_LINES - 1);
		itemMenu.setHoveredIndex(selectedIndex);
	}
	
	private void selectCurrentIndex() {
		itemMenu.selectHoveredItem();
		SpecialAction specialAction = SpecialAction.findByMenuIndex(selectedIndex);
		character.setActiveSpecialAction(specialAction);
	}
	
	private boolean isEventTypeHandled(Type type) {
		return type == Type.KEY_DOWN || type == Type.CONTROLLER_BUTTON_PRESSED || type == Type.CONTROLLER_AXIS_THRESHOLD_PASSED
				|| type == Type.CONTROLLER_POV_CHANGED;
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
		drawBackground();
		drawItemMenu();
		drawBanners();
		batch.end();
		
		drawTexts();
	}
	
	private void drawBackground() {
		gameSnapshotSprite.draw(batch);
		background.draw(batch, 100, 100, 980, 600);
	}
	
	private void drawItemMenu() {
		itemMenu.draw(batch, 625, 150, 400, 200);
	}
	
	private void drawBanners() {
		banner.draw(batch, 125, 540, 650, 250);
		itemMenuBanner.draw(batch, 640, 260, 200, 150);
	}
	
	private void drawTexts() {
		screenTextWriter.setColor(Color.BLACK);
		
		screenTextWriter.setScale(1.5f);
		screenTextWriter.drawText("Pause Menu", 230, 683);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText("Items", 690, 345);
	}
	
	@Override
	public void dispose() {
		assetManager.unloadGroup(ASSET_GROUP_NAME);
		DwarfScrollerGame.getInstance().getInputContext().removeListener(this);
		
		gameSnapshotFrameBuffer.dispose();
	}
}
