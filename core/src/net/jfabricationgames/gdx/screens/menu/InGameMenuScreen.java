package net.jfabricationgames.gdx.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.assets.AssetGroupManager;
import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.character.SpecialAction;
import net.jfabricationgames.gdx.debug.DebugGridRenderer;
import net.jfabricationgames.gdx.input.InputActionListener;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screens.menu.components.ItemMenu;
import net.jfabricationgames.gdx.screens.menu.components.MenuBackground;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;
import net.jfabricationgames.gdx.screens.menu.control.ControlledMenu;
import net.jfabricationgames.gdx.screens.menu.control.MenuStateMachine.InputDirection;
import net.jfabricationgames.gdx.text.ScreenTextWriter;

public class InGameMenuScreen extends ControlledMenu<InGameMenuScreen> implements InputActionListener {
	
	private static final String TEXT_COLOR_ENCODING_NORMAL = "[#000000]";
	private static final String TEXT_COLOR_ENCODING_FOCUS = "[#C8441B]";
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
	
	private static final String BUTTON_CONFIG_FOCUSED_FILE = "config/menu/buttons/green_button_focused_nine_patch.json";
	private static final String BUTTON_CONFIG_FILE = "config/menu/buttons/green_button_nine_patch.json";
	private static final float BUTTON_SCALE = 2f;
	
	private static final String statePrefixItems = "item_";
	private static final String statePrefixButtons = "button_";
	
	private static final String inGameMenuStatesConfig = "config/menu/in_game_menu_states.json";
	private static final Array<String> items = new Array<String>(new String[] {"jump", "bow"});
	
	private Viewport viewport;
	
	private GameScreen gameScreen;
	private PlayableCharacter character;
	private AssetGroupManager assetManager;
	private ScreenTextWriter screenTextWriter;
	private DebugGridRenderer debugGridRenderer;
	
	private SpriteBatch batch;
	private FrameBuffer gameSnapshotFrameBuffer;
	private Sprite gameSnapshotSprite;
	
	private MenuBox background;
	private MenuBox banner;
	private ItemMenu itemMenu;
	private MenuBox itemMenuBanner;
	private FocusButton buttonBackToGame;
	private FocusButton buttonControls;
	private FocusButton buttonRestart;
	private FocusButton buttonQuit;
	
	public InGameMenuScreen(GameScreen gameScreen, PlayableCharacter character) {
		super(inGameMenuStatesConfig);
		this.gameScreen = gameScreen;
		this.character = character;
		
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		gameSnapshotFrameBuffer = new FrameBuffer(Format.RGB888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);
		batch = new SpriteBatch();
		
		assetManager = AssetGroupManager.getInstance();
		assetManager.loadGroup(ASSET_GROUP_NAME);
		assetManager.finishLoading();
		
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(FONT_NAME);
		
		debugGridRenderer = new DebugGridRenderer();
		debugGridRenderer.setLineOffsets(50f, 50f);
		debugGridRenderer.stopDebug();
		
		initialize();
	}
	
	private void initialize() {
		createComponents();
		
		itemMenu.setHoveredIndex(0);
		itemMenu.selectHoveredItem();
		
		stateMachine.changeToInitialState();
	}
	
	private void createComponents() {
		background = new MenuBackground(12, 8, MenuBox.TextureType.GREEN_BOARD);
		banner = new MenuBackground(6, 2, MenuBox.TextureType.BIG_BANNER);
		
		itemMenu = new ItemMenu(ITEM_MENU_ITEMS_PER_LINE, ITEM_MENU_LINES, items);
		itemMenuBanner = new MenuBackground(4, 2, MenuBox.TextureType.BIG_BANNER);
		
		int buttonWidth = 290;
		int buttonHeight = 55;
		int buttonPosX = 160;
		int lowestButtonY = 150;
		int buttonGapY = 40;
		buttonBackToGame = new FocusButtonBuilder().setNinePatchConfig(BUTTON_CONFIG_FILE).setNinePatchConfigFocused(BUTTON_CONFIG_FOCUSED_FILE)
				.setSize(buttonWidth, buttonHeight).setPosition(buttonPosX, lowestButtonY + 3f * (buttonHeight + buttonGapY)).build();
		buttonControls = new FocusButtonBuilder().setNinePatchConfig(BUTTON_CONFIG_FILE).setNinePatchConfigFocused(BUTTON_CONFIG_FOCUSED_FILE)
				.setSize(buttonWidth, buttonHeight).setPosition(buttonPosX, lowestButtonY + 2f * (buttonHeight + buttonGapY)).build();
		buttonRestart = new FocusButtonBuilder().setNinePatchConfig(BUTTON_CONFIG_FILE).setNinePatchConfigFocused(BUTTON_CONFIG_FOCUSED_FILE)
				.setSize(buttonWidth, buttonHeight).setPosition(buttonPosX, lowestButtonY + 1f * (buttonHeight + buttonGapY)).build();
		buttonQuit = new FocusButtonBuilder().setNinePatchConfig(BUTTON_CONFIG_FILE).setNinePatchConfigFocused(BUTTON_CONFIG_FOCUSED_FILE)
				.setSize(buttonWidth, buttonHeight).setPosition(buttonPosX, lowestButtonY).build();
		
		buttonBackToGame.scaleBy(BUTTON_SCALE);
		buttonControls.scaleBy(BUTTON_SCALE);
		buttonRestart.scaleBy(BUTTON_SCALE);
		buttonQuit.scaleBy(BUTTON_SCALE);
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (action.equals(ACTION_BACK_TO_GAME) && isEventTypeHandled(type)) {
			backToGame();
			return true;
		}
		if (action.equals(ACTION_SELECTION_UP) && isEventTypeHandled(type)) {
			stateMachine.changeState(InputDirection.UP);
		}
		if (action.equals(ACTION_SELECTION_DOWN) && isEventTypeHandled(type)) {
			stateMachine.changeState(InputDirection.DOWN);
		}
		if (action.equals(ACTION_SELECTION_LEFT) && isEventTypeHandled(type)) {
			stateMachine.changeState(InputDirection.LEFT);
		}
		if (action.equals(ACTION_SELECTION_RIGHT) && isEventTypeHandled(type)) {
			stateMachine.changeState(InputDirection.RIGHT);
		}
		if (action.equals(ACTION_SELECT) && isEventTypeHandled(type)) {
			stateMachine.selectActionOnCurrentState();
		}
		return false;
	}
	
	public void selectCurrentItem() {
		itemMenu.selectHoveredItem();
		SpecialAction specialAction = SpecialAction.findByNameIgnoringCase(itemMenu.getSelectedItem());
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
	
	public void backToGame() {
		DwarfScrollerGame game = DwarfScrollerGame.getInstance();
		game.getInputContext().removeListener(this);
		game.changeInputContext(GameScreen.INPUT_CONTEXT_NAME);
		game.setScreen(gameScreen);
	}
	
	public void showControls() {
		//TODO change to a new controls menu
	}
	
	public void restartGame() {
		//TODO starting a new game causes problems with singletons
		//DwarfScrollerGame.getInstance().setScreen(new GameScreen());
		//dispose();
	}
	
	public void quitGame() {
		Gdx.app.exit();
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
		drawButtons();
		drawBanners();
		batch.end();
		
		drawTexts();
		
		debugGridRenderer.render(delta);
	}
	
	private void drawBackground() {
		gameSnapshotSprite.draw(batch);
		background.draw(batch, 100, 100, 980, 600);
	}
	
	private void drawItemMenu() {
		itemMenu.draw(batch, 625, 150, 400, 200);
	}
	
	private void drawButtons() {
		buttonBackToGame.draw(batch);
		buttonControls.draw(batch);
		buttonRestart.draw(batch);
		buttonQuit.draw(batch);
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
		
		screenTextWriter.setScale(1.15f);
		int buttonTextX = 160;
		int buttonTextWidth = 430;
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToGame) + "Back to Game", buttonTextX, 494, buttonTextWidth, Align.center,
				false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonControls) + "Controlls", buttonTextX, 397, buttonTextWidth, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonRestart) + "Restart Game", buttonTextX, 302, buttonTextWidth, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonQuit) + "Quit", buttonTextX, 206, buttonTextWidth, Align.center, false);
	}
	
	private String getButtonTextColorEncoding(FocusButton button) {
		return button.hasFocus() ? TEXT_COLOR_ENCODING_FOCUS : TEXT_COLOR_ENCODING_NORMAL;
	}
	
	@Override
	public void dispose() {
		assetManager.unloadGroup(ASSET_GROUP_NAME);
		DwarfScrollerGame.getInstance().getInputContext().removeListener(this);
		
		gameSnapshotFrameBuffer.dispose();
	}
	
	@Override
	protected void setFocusTo(String stateName, String leavingState) {
		unfocusAll();
		if (stateName.startsWith(statePrefixItems)) {
			int itemIndex = Integer.parseInt(stateName.substring(statePrefixItems.length())) - 1;
			itemMenu.setHoveredIndex(itemIndex);
		}
		else if (stateName.startsWith(statePrefixButtons)) {
			String buttonId = stateName.substring(statePrefixButtons.length());
			FocusButton button;
			switch (buttonId) {
				case "backToGame":
					button = buttonBackToGame;
					break;
				case "controls":
					button = buttonControls;
					break;
				case "restartGame":
					button = buttonRestart;
					break;
				case "quit":
					button = buttonQuit;
					break;
				default:
					throw new IllegalStateException("Unexpected button state identifier: " + statePrefixButtons + buttonId);
			}
			button.setFocused(true);
		}
	}
	
	private void unfocusAll() {
		buttonBackToGame.setFocused(false);
		buttonControls.setFocused(false);
		buttonRestart.setFocused(false);
		buttonQuit.setFocused(false);
		itemMenu.setHoveredIndex(-1);
	}
}
