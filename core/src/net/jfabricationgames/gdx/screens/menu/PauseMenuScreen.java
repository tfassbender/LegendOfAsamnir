package net.jfabricationgames.gdx.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.character.SpecialAction;
import net.jfabricationgames.gdx.debug.DebugGridRenderer;
import net.jfabricationgames.gdx.item.ItemAmmoType;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.screens.menu.components.AmmoSubMenu;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screens.menu.components.GameControlsDialog;
import net.jfabricationgames.gdx.screens.menu.components.ItemSubMenu;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;

public class PauseMenuScreen extends InGameMenuScreen<PauseMenuScreen> {
	
	private static final String INPUT_CONTEXT_NAME = "pauseMenu";
	
	private static final String SOUND_ENTER_PAUSE_MENU = "enter_pause_menu";
	private static final String ACTION_BACK_TO_GAME = "backToGame";
	
	private static final int ITEM_MENU_ITEMS_PER_LINE = 4;
	private static final int ITEM_MENU_LINES = 2;
	
	private static final String statePrefixItems = "item_";
	private static final String statePrefixButtons = "button_";
	
	private static final String pauseMenuStatesConfig = "config/menu/in_game_menu_states.json";
	private static final Array<String> items = new Array<>(new String[] {"jump", "bow", "bomb"});
	private static final Array<ItemAmmoType> ammoItems = new Array<>(new ItemAmmoType[] {ItemAmmoType.ARROW, ItemAmmoType.BOMB});
	
	private DebugGridRenderer debugGridRenderer;
	
	private GameControlsDialog controlsDialog;
	
	private MenuBox background;
	private MenuBox headerBanner;
	private ItemSubMenu itemMenu;
	private MenuBox itemMenuBanner;
	private AmmoSubMenu ammoMenu;
	private MenuBox ammoMenuBanner;
	private FocusButton buttonBackToGame;
	private FocusButton buttonControls;
	private FocusButton buttonRestart;
	private FocusButton buttonQuit;
	
	public PauseMenuScreen(GameScreen gameScreen, PlayableCharacter player) {
		super(pauseMenuStatesConfig, gameScreen, player);
		
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
		background = new MenuBox(12, 8, MenuBox.TextureType.GREEN_BOARD);
		headerBanner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		
		itemMenu = new ItemSubMenu(ITEM_MENU_ITEMS_PER_LINE, ITEM_MENU_LINES, items);
		itemMenuBanner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER);
		
		int buttonWidth = 290;
		int buttonHeight = 55;
		int buttonPosX = 160;
		int lowestButtonY = 150;
		int buttonGapY = 40;
		buttonBackToGame = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG)
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED).setSize(buttonWidth, buttonHeight)
				.setPosition(buttonPosX, lowestButtonY + 3f * (buttonHeight + buttonGapY)).build();
		buttonControls = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG)
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED).setSize(buttonWidth, buttonHeight)
				.setPosition(buttonPosX, lowestButtonY + 2f * (buttonHeight + buttonGapY)).build();
		buttonRestart = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG)
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED).setSize(buttonWidth, buttonHeight)
				.setPosition(buttonPosX, lowestButtonY + 1f * (buttonHeight + buttonGapY)).build();
		buttonQuit = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG)
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED).setSize(buttonWidth, buttonHeight)
				.setPosition(buttonPosX, lowestButtonY).build();
		
		buttonBackToGame.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonControls.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonRestart.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonQuit.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		ammoMenu = new AmmoSubMenu(ammoItems, player);
		ammoMenuBanner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER_LOW);
		
		controlsDialog = new GameControlsDialog();
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (action.equals(ACTION_BACK_TO_GAME) && isEventTypeHandled(type)) {
			backToGame();
			return true;
		}
		
		return super.onAction(action, type, parameters);
	}
	
	public void selectCurrentItem() {
		itemMenu.selectHoveredItem();
		SpecialAction specialAction = SpecialAction.findByNameIgnoringCase(itemMenu.getSelectedItem());
		player.setActiveSpecialAction(specialAction);
	}
	
	@Override
	public void showMenu() {
		super.showMenu();
		
		takeGameSnapshot();
		playMenuSound(SOUND_ENTER_PAUSE_MENU);
	}
	
	@Override
	protected String getInputContextName() {
		return INPUT_CONTEXT_NAME;
	}
	
	public void showControls() {
		Gdx.app.debug(getClass().getSimpleName(), "'Show Controlls' selected");
		controlsDialog.setVisible(true);
		stateMachine.changeState("button_controlsDialogBack");
	}
	
	public void closeControlsDialog() {
		controlsDialog.setVisible(false);
		stateMachine.changeState("button_controls");
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		drawBackground();
		drawItemMenu();
		drawAmmoMenu();
		drawButtons();
		drawBanners();
		batch.end();
		
		drawTexts();
		
		drawControlsDialog();
		
		debugGridRenderer.render(delta);
	}
	
	private void drawBackground() {
		gameSnapshotSprite.draw(batch);
		background.draw(batch, 100, 100, 980, 600);
	}
	
	private void drawItemMenu() {
		itemMenu.draw(batch, 625, 150, 400, 200);
	}
	
	private void drawAmmoMenu() {
		ammoMenu.draw(batch, 825, 370, 200, 110);
	}
	
	private void drawButtons() {
		buttonBackToGame.draw(batch);
		buttonControls.draw(batch);
		buttonRestart.draw(batch);
		buttonQuit.draw(batch);
	}
	
	private void drawBanners() {
		headerBanner.draw(batch, 125, 540, 650, 250);
		itemMenuBanner.draw(batch, 640, 260, 200, 150);
		ammoMenuBanner.draw(batch, 800, 415, 250, 150);
	}
	
	private void drawControlsDialog() {
		controlsDialog.draw();
	}
	
	private void drawTexts() {
		ammoMenu.drawAmmoTexts();
		
		screenTextWriter.setColor(Color.BLACK);
		
		screenTextWriter.setScale(1.5f);
		screenTextWriter.drawText("Pause Menu", 230, 683);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText("Items", 690, 345);
		
		screenTextWriter.drawText("Ammo", 860, 500);
		
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
	protected void setFocusTo(String stateName, String leavingState) {
		unfocusAll();
		if (stateName.startsWith(statePrefixItems)) {
			int itemIndex = Integer.parseInt(stateName.substring(statePrefixItems.length())) - 1;
			itemMenu.setHoveredIndex(itemIndex);
		}
		else if (stateName.startsWith(statePrefixButtons)) {
			String buttonId = stateName.substring(statePrefixButtons.length());
			FocusButton button = null;
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
				case "controlsDialogBack":
					//dialog button; not handled here
					break;
				default:
					throw new IllegalStateException("Unexpected button state identifier: " + statePrefixButtons + buttonId);
			}
			if (button != null) {
				button.setFocused(true);
			}
		}
	}
	
	private void unfocusAll() {
		buttonBackToGame.setFocused(false);
		buttonControls.setFocused(false);
		buttonRestart.setFocused(false);
		buttonQuit.setFocused(false);
		itemMenu.setHoveredIndex(-1);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		removeInputListener();
		controlsDialog.dispose();
	}
}
