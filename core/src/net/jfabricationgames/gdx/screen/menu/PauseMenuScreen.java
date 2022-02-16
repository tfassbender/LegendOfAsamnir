package net.jfabricationgames.gdx.screen.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;

import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.animation.TextureAnimationDirector;
import net.jfabricationgames.gdx.character.player.implementation.SpecialAction;
import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.screen.ScreenManager;
import net.jfabricationgames.gdx.screen.menu.components.AmmoSubMenu;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screen.menu.components.ItemSubMenu;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;
import net.jfabricationgames.gdx.screen.menu.components.RuneSubMenu;
import net.jfabricationgames.gdx.screen.menu.components.SpecialActionItemSubMenu;
import net.jfabricationgames.gdx.screen.menu.control.MenuStateMachine;
import net.jfabricationgames.gdx.screen.menu.dialog.GameControlsDialog;
import net.jfabricationgames.gdx.screen.menu.dialog.LoadGameDialog;
import net.jfabricationgames.gdx.screen.menu.dialog.SaveGameDialog;

public class PauseMenuScreen extends InGameMenuScreen<PauseMenuScreen> {
	
	public static final String INPUT_CONTEXT_NAME = "pauseMenu";
	
	private static final String SOUND_ENTER_PAUSE_MENU = "enter_pause_menu";
	private static final String ACTION_BACK = "back";
	
	private static final String STATE_PREFIX_ITEM = "item_";
	private static final String STATE_PREFIX_BUTTON = "button_";
	private static final String STATE_PREFIX_RUNE = "rune_";
	
	private static final String MAP_ANIMATION_IDLE = "map_idle";
	private static final String PAUSE_MENU_STATES_CONFIG = "config/menu/pause_menu_states.json";
	
	private static final String STATE_PREFIX_MAP_DIALOG = "mapDialog_";
	private static final String STATE_PREFIX_SAVE_DIALOG = "saveDialog_";
	private static final String STATE_PREFIX_LOAD_DIALOG = "loadDialog_";
	
	private GameControlsDialog controlsDialog;
	private GameMapDialog mapDialog;
	private SaveGameDialog saveGameDialog;
	private LoadGameDialog loadGameDialog;
	
	private MenuBox background;
	
	private MenuBox headerBanner;
	
	private ItemSubMenu itemMenu;
	private MenuBox itemMenuBanner;
	
	private RuneSubMenu runeMenu;
	private MenuBox runeMenuBanner;
	private MenuBox runeDescriptionBanner;
	
	private AmmoSubMenu ammoMenu;
	private MenuBox ammoMenuBanner;
	
	private MenuBox mapBanner;
	private TextureAnimationDirector<TextureRegion> mapAnimation;
	
	private FocusButton buttonBackToGame;
	private FocusButton buttonControls;
	private FocusButton buttonSave;
	private FocusButton buttonLoad;
	private FocusButton buttonMainMenu;
	private FocusButton buttonShowMap;
	
	public PauseMenuScreen(MenuGameScreen gameScreen) {
		super(gameScreen, (String[]) null);
		
		initialize();
	}
	
	private void initialize() {
		createComponents();
		createDialogs();
		
		itemMenu.setHoveredIndex(0);
		itemMenu.selectHoveredItem();
		
		initializeStateMachine();
		
		//adapt the camera position to center the menu on the screen (instead of changing all components)
		camera.position.y -= 20;
	}
	
	private void createComponents() {
		background = new MenuBox(12, 10, MenuBox.TextureType.GREEN_BOARD);
		headerBanner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		
		itemMenu = new SpecialActionItemSubMenu();
		itemMenuBanner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER);
		
		runeMenu = new RuneSubMenu();
		runeMenuBanner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER);
		runeDescriptionBanner = new MenuBox(5, 2, MenuBox.TextureType.BIG_BANNER_LOW);
		
		int buttonWidth = 290;
		int buttonHeight = 55;
		int buttonPosX = 160;
		int lowestButtonY = 220;
		int buttonGapY = 40;
		buttonBackToGame = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED) //
				.setSize(buttonWidth, buttonHeight) //
				.setPosition(buttonPosX, lowestButtonY + 4f * (buttonHeight + buttonGapY)) //
				.build();
		buttonControls = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED) //
				.setSize(buttonWidth, buttonHeight) //
				.setPosition(buttonPosX, lowestButtonY + 3f * (buttonHeight + buttonGapY)) //
				.build();
		buttonSave = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED) //
				.setSize(buttonWidth, buttonHeight) //
				.setPosition(buttonPosX, lowestButtonY + 2f * (buttonHeight + buttonGapY)) //
				.build();
		buttonLoad = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED)//
				.setSize(buttonWidth, buttonHeight) //
				.setPosition(buttonPosX, lowestButtonY + 1f * (buttonHeight + buttonGapY)) //
				.build();
		buttonMainMenu = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED) //
				.setSize(buttonWidth, buttonHeight) //
				.setPosition(buttonPosX, lowestButtonY) //
				.build();
		
		buttonBackToGame.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonControls.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonSave.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonLoad.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonMainMenu.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		ammoMenu = new AmmoSubMenu(player);
		ammoMenuBanner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER_LOW);
		
		mapBanner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER_LOW);
		buttonShowMap = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED) //
				.setSize(120, 35) //
				.setPosition(625, 443) //
				.build();
		buttonShowMap.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		mapAnimation = AnimationManager.getInstance().getTextureAnimationDirector(MAP_ANIMATION_IDLE);
	}
	
	private void createDialogs() {
		controlsDialog = new GameControlsDialog(camera);
		mapDialog = new GameMapDialog(gameScreen, camera, this::backToGame, this::playMenuSound);
		saveGameDialog = new SaveGameDialog(camera, this::backToGame, this::playMenuSound);
		loadGameDialog = new LoadGameDialog(camera, this::backToGame, this::playMenuSound);
	}
	
	private void initializeStateMachine() {
		stateMachine = new MenuStateMachine(this, PAUSE_MENU_STATES_CONFIG, mapDialog.getMapStateConfigFile());
		if (!Constants.DEBUG) {
			stateMachine.removeDebugStates();
		}
		
		stateMachine.changeToInitialState();
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (action.equals(ACTION_BACK) && isEventTypeHandled(type)) {
			if (controlsDialog.isVisible()) {
				closeControlsDialog();
			}
			else if (mapDialog.isVisible()) {
				closeMapDialog();
			}
			else if (saveGameDialog.isVisible()) {
				closeSaveGameDialog();
			}
			else if (loadGameDialog.isVisible()) {
				closeLoadGameDialog();
			}
			else {
				backToGame();
			}
			
			return true;
		}
		
		return super.onAction(action, type, parameters);
	}
	
	@Override
	public void showMenu() {
		super.showMenu();
		
		closeControlsDialog();
		closeMapDialog();
		closeSaveGameDialog();
		closeLoadGameDialog();
		mapDialog.updateMapConfig(gameScreen);
		
		takeGameSnapshot();
		playMenuSound(SOUND_ENTER_PAUSE_MENU);
		
		stateMachine.changeToInitialState();
		
		itemMenu.setSelectedIndex(player.getActiveSpecialAction().indexInMenu);
		itemMenu.updateStateAfterMenuShown();
		runeMenu.updateStateAfterMenuShown();
	}
	
	@Override
	protected String getInputContextName() {
		return INPUT_CONTEXT_NAME;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		setProjectionMatrixBeforeRendering();
		
		batch.begin();
		drawBackground();
		drawItemMenu();
		drawRuneMenu();
		drawMap(delta);
		drawAmmoMenu();
		drawButtons();
		drawBanners();
		batch.end();
		
		drawTexts();
		
		drawControlsDialog();
		drawMapDialog(delta);
		drawSaveGameDialog();
		drawLoadGameDialog();
	}
	
	private void drawBackground() {
		gameSnapshotSprite.draw(batch);
		background.draw(batch, 100, 0, 980, 770);
	}
	
	private void drawItemMenu() {
		itemMenu.draw(batch, 625, 220, 400, 200);
	}
	
	private void drawRuneMenu() {
		runeMenu.draw(batch, 160, 55, 865, 140);
	}
	
	private void drawMap(float delta) {
		mapAnimation.setSpriteConfig(new AnimationSpriteConfig().setX(670).setY(500).setWidth(80).setHeight(80));
		if (buttonShowMap.hasFocus()) {
			mapAnimation.increaseStateTime(delta);
		}
		else {
			mapAnimation.resetStateTime();
		}
		mapAnimation.drawInMenu(batch);
	}
	
	private void drawAmmoMenu() {
		ammoMenu.setBatchProjectionMatrix(camera.combined);
		ammoMenu.draw(batch, 825, 450, 200, 110);
	}
	
	private void drawButtons() {
		buttonBackToGame.draw(batch);
		buttonControls.draw(batch);
		buttonSave.draw(batch);
		buttonLoad.draw(batch);
		buttonMainMenu.draw(batch);
		buttonShowMap.draw(batch);
	}
	
	private void drawBanners() {
		headerBanner.draw(batch, 125, 610, 650, 250);
		itemMenuBanner.draw(batch, 640, 330, 200, 150);
		runeMenuBanner.draw(batch, 180, 105, 200, 150);
		runeDescriptionBanner.draw(batch, 210, -5, 890, 150);
		ammoMenuBanner.draw(batch, 800, 535, 250, 150);
		mapBanner.draw(batch, 610, 535, 200, 150);
	}
	
	private void drawControlsDialog() {
		controlsDialog.draw();
	}
	
	private void drawMapDialog(float delta) {
		mapDialog.draw(delta);
	}
	
	private void drawSaveGameDialog() {
		saveGameDialog.draw();
	}
	
	private void drawLoadGameDialog() {
		loadGameDialog.draw();
	}
	
	private void drawTexts() {
		ammoMenu.drawAmmoTexts();
		
		screenTextWriter.setColor(Color.BLACK);
		
		screenTextWriter.setScale(1.5f);
		screenTextWriter.drawText("Pause Menu", 230, 753);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText("Items", 690, 415);
		
		screenTextWriter.drawText("Ammo", 860, 620);
		
		screenTextWriter.drawText("Map", 670, 620);
		
		screenTextWriter.drawText("Runes", 223, 190);
		
		screenTextWriter.setScale(0.55f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonShowMap) + "Show Map", 675, 478, 80, Align.center, false);
		
		screenTextWriter.drawText(runeMenu.getHoveredRuneDescription(), 330, 75);
		
		screenTextWriter.setScale(1.15f);
		int buttonTextX = 160;
		int buttonTextWidth = 430;
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToGame) + "Back to Game", buttonTextX, 661, buttonTextWidth, Align.center,
				false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonControls) + "Controlls", buttonTextX, 564, buttonTextWidth, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonSave) + "Save Game", buttonTextX, 467, buttonTextWidth, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonLoad) + "Load Game", buttonTextX, 372, buttonTextWidth, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonMainMenu) + "Main Menu", buttonTextX, 276, buttonTextWidth, Align.center, false);
	}
	
	private String getButtonTextColorEncoding(FocusButton button) {
		return button.hasFocus() ? TEXT_COLOR_ENCODING_FOCUS : TEXT_COLOR_ENCODING_NORMAL;
	}
	
	@Override
	public void setFocusTo(String stateName, String leavingState) {
		unfocusAll();
		if (stateName.startsWith(STATE_PREFIX_ITEM)) {
			int itemIndex = Integer.parseInt(stateName.substring(STATE_PREFIX_ITEM.length())) - 1;
			itemMenu.setHoveredIndex(itemIndex);
		}
		else if (stateName.startsWith(STATE_PREFIX_RUNE)) {
			int runeIndex = Integer.parseInt(stateName.substring(STATE_PREFIX_RUNE.length())) - 1;
			runeMenu.setHoveredIndex(runeIndex);
		}
		else if (stateName.startsWith(STATE_PREFIX_MAP_DIALOG)) {
			if (stateName.equals(STATE_PREFIX_MAP_DIALOG + "button_mapDialogBack")) {
				mapDialog.setFocusToBackButton();
			}
		}
		else if (stateName.startsWith(STATE_PREFIX_SAVE_DIALOG)) {
			saveGameDialog.setFocusTo(stateName);
		}
		else if (stateName.startsWith(STATE_PREFIX_LOAD_DIALOG)) {
			loadGameDialog.setFocusTo(stateName);
		}
		else if (stateName.startsWith(STATE_PREFIX_BUTTON)) {
			String buttonId = stateName.substring(STATE_PREFIX_BUTTON.length());
			FocusButton button = null;
			switch (buttonId) {
				case "backToGame":
					button = buttonBackToGame;
					break;
				case "controls":
					button = buttonControls;
					break;
				case "saveGame":
					button = buttonSave;
					break;
				case "loadGame":
					button = buttonLoad;
					break;
				case "main_menu":
					button = buttonMainMenu;
					break;
				case "showMap":
					button = buttonShowMap;
					break;
				case "controlsDialogBack":
					//dialog button; not handled here
					break;
				default:
					throw new IllegalStateException("Unexpected button state identifier: " + STATE_PREFIX_BUTTON + buttonId);
			}
			if (button != null) {
				button.setFocused(true);
			}
		}
		
		//no prefix is used for fast travel points
		//will be ignored if the state is not known in the map dialog
		mapDialog.setFocusTo(stateName);
	}
	
	private void unfocusAll() {
		buttonBackToGame.setFocused(false);
		buttonControls.setFocused(false);
		buttonSave.setFocused(false);
		buttonLoad.setFocused(false);
		buttonMainMenu.setFocused(false);
		buttonShowMap.setFocused(false);
		itemMenu.setHoveredIndex(-1);
		runeMenu.setHoveredIndex(-1);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		controlsDialog.dispose();
		mapDialog.dispose();
		saveGameDialog.dispose();
		loadGameDialog.dispose();
	}
	
	//****************************************************************
	//*** State machine methods (called via reflection)
	//****************************************************************
	
	public void showControls() {
		Gdx.app.debug(getClass().getSimpleName(), "'Show Controlls' selected");
		controlsDialog.setVisible(true);
		stateMachine.changeState("button_controlsDialogBack");
	}
	
	public void saveGame() {
		Gdx.app.debug(getClass().getSimpleName(), "'Save Game' selected");
		saveGameDialog.setVisible(true);
		stateMachine.changeState("saveDialog_button_saveGameDialogBack");
	}
	
	public void loadGame() {
		Gdx.app.debug(getClass().getSimpleName(), "'Load Game' selected");
		loadGameDialog.setVisible(true);
		stateMachine.changeState("loadDialog_button_loadGameDialogBack");
	}
	
	public void backToMainMenu() {
		Gdx.app.debug(getClass().getSimpleName(), "'Main Menu' selected");
		removeInputListener();
		gameScreen.dispose();
		
		ScreenManager.getInstance().changeToMainMenuScreen();
	}
	
	public void showMap() {
		Gdx.app.debug(getClass().getSimpleName(), "'Show Map' selected");
		mapDialog.loadConfig(gameScreen.getGameMapConfigPath());
		mapDialog.setVisible(true);
		mapDialog.setFocusToBackButton();
		stateMachine.changeState("mapDialog_button_mapDialogBack");
	}
	
	public void selectCurrentItem() {
		itemMenu.selectHoveredItem();
		SpecialAction specialAction = SpecialAction.findByNameIgnoringCase(itemMenu.getSelectedItem());
		player.setActiveSpecialAction(specialAction);
	}
	
	public void closeControlsDialog() {
		controlsDialog.setVisible(false);
		stateMachine.changeState("button_controls");
	}
	
	public void closeMapDialog() {
		mapDialog.setVisible(false);
		stateMachine.changeState("button_showMap");
	}
	
	public void closeSaveGameDialog() {
		saveGameDialog.setVisible(false);
		stateMachine.changeState("button_saveGame");
	}
	
	public void closeLoadGameDialog() {
		loadGameDialog.setVisible(false);
		stateMachine.changeState("button_loadGame");
	}
	
	public void selectFastTravelPoint() {
		mapDialog.selectFastTravelPoint();
	}
	
	public void noAction() {}
	
	//*********************************************************************
	//*** State machine methods for save dialog (called via reflection)
	//*********************************************************************
	
	public void quickSave() {
		saveGameDialog.quickSave();
	}
	
	public void saveToSlot1() {
		saveGameDialog.saveToSlot(1);
	}
	
	public void saveToSlot2() {
		saveGameDialog.saveToSlot(2);
	}
	
	public void saveToSlot3() {
		saveGameDialog.saveToSlot(3);
	}
	
	public void saveToSlot4() {
		saveGameDialog.saveToSlot(4);
	}
	
	public void saveToSlot5() {
		saveGameDialog.saveToSlot(5);
	}
	
	//*********************************************************************
	//*** State machine methods for load dialog (called via reflection)
	//*********************************************************************
	
	public void loadFromQuickSaveSlot() {
		loadGameDialog.loadFromQuickSaveSlot();
	}
	
	public void loadFromSlot1() {
		loadGameDialog.loadFromSlot(1);
	}
	
	public void loadFromSlot2() {
		loadGameDialog.loadFromSlot(2);
	}
	
	public void loadFromSlot3() {
		loadGameDialog.loadFromSlot(3);
	}
	
	public void loadFromSlot4() {
		loadGameDialog.loadFromSlot(4);
	}
	
	public void loadFromSlot5() {
		loadGameDialog.loadFromSlot(5);
	}
}
