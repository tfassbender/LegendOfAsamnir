package net.jfabricationgames.gdx.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.animation.AnimationSpriteConfig;
import net.jfabricationgames.gdx.character.PlayableCharacter;
import net.jfabricationgames.gdx.character.SpecialAction;
import net.jfabricationgames.gdx.item.ItemAmmoType;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.screens.menu.components.AmmoSubMenu;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screens.menu.components.ItemSubMenu;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;
import net.jfabricationgames.gdx.screens.menu.control.MenuStateMachine;
import net.jfabricationgames.gdx.screens.menu.dialog.GameControlsDialog;
import net.jfabricationgames.gdx.screens.menu.dialog.GameMapDialog;

public class PauseMenuScreen extends InGameMenuScreen<PauseMenuScreen> {
	
	public static final String INPUT_CONTEXT_NAME = "pauseMenu";
	
	private static final String SOUND_ENTER_PAUSE_MENU = "enter_pause_menu";
	private static final String ACTION_BACK_TO_GAME = "backToGame";
	
	private static final int ITEM_MENU_ITEMS_PER_LINE = 4;
	private static final int ITEM_MENU_LINES = 2;
	
	private static final String STATE_PREFIX_ITEM = "item_";
	private static final String STATE_PREFIX_BUTTON = "button_";
	
	private static final String MAP_ANIMATION_IDLE = "map_idle";
	private static final String PAUSE_MENU_STATES_CONFIG = "config/menu/pause_menu_states.json";
	
	private static final Array<String> ITEMS = new Array<>(new String[] {"jump", "bow", "bomb"});
	private static final Array<ItemAmmoType> AMMO_ITEMS = new Array<>(new ItemAmmoType[] {ItemAmmoType.ARROW, ItemAmmoType.BOMB});
	
	private GameControlsDialog controlsDialog;
	private GameMapDialog mapDialog;
	
	private MenuBox background;
	private MenuBox headerBanner;
	private ItemSubMenu itemMenu;
	private MenuBox itemMenuBanner;
	private AmmoSubMenu ammoMenu;
	private MenuBox ammoMenuBanner;
	private MenuBox mapBanner;
	private AnimationDirector<TextureRegion> mapAnimation;
	private FocusButton buttonBackToGame;
	private FocusButton buttonControls;
	private FocusButton buttonRestart;
	private FocusButton buttonQuit;
	private FocusButton buttonShowMap;
	
	public PauseMenuScreen(GameScreen gameScreen, PlayableCharacter player) {
		super(gameScreen, player, (String[]) null);
		
		initialize();
	}
	
	private void initialize() {
		createComponents();
		createDialogs();
		
		itemMenu.setHoveredIndex(0);
		itemMenu.selectHoveredItem();
		
		initializeStateMachine();
	}
	
	private void createComponents() {
		background = new MenuBox(12, 8, MenuBox.TextureType.GREEN_BOARD);
		headerBanner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		
		itemMenu = new ItemSubMenu(ITEM_MENU_ITEMS_PER_LINE, ITEM_MENU_LINES, ITEMS);
		itemMenuBanner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER);
		
		int buttonWidth = 290;
		int buttonHeight = 55;
		int buttonPosX = 160;
		int lowestButtonY = 150;
		int buttonGapY = 40;
		buttonBackToGame = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED) //
				.setSize(buttonWidth, buttonHeight) //
				.setPosition(buttonPosX, lowestButtonY + 3f * (buttonHeight + buttonGapY)) //
				.build();
		buttonControls = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED) //
				.setSize(buttonWidth, buttonHeight) //
				.setPosition(buttonPosX, lowestButtonY + 2f * (buttonHeight + buttonGapY)) //
				.build();
		buttonRestart = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED) //
				.setSize(buttonWidth, buttonHeight) //
				.setPosition(buttonPosX, lowestButtonY + 1f * (buttonHeight + buttonGapY)) //
				.build();
		buttonQuit = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED) //
				.setSize(buttonWidth, buttonHeight) //
				.setPosition(buttonPosX, lowestButtonY) //
				.build();
		
		buttonBackToGame.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonControls.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonRestart.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonQuit.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		ammoMenu = new AmmoSubMenu(AMMO_ITEMS, player);
		ammoMenuBanner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER_LOW);
		
		mapBanner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER_LOW);
		buttonShowMap = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED) //
				.setSize(120, 35) //
				.setPosition(625, 373) //
				.build();
		buttonShowMap.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		mapAnimation = AnimationManager.getInstance().getAnimationDirector(MAP_ANIMATION_IDLE);
	}
	
	private void createDialogs() {
		controlsDialog = new GameControlsDialog();
		mapDialog = new GameMapDialog(gameScreen, this::backToGame, this::playMenuSound);
	}
	
	private void initializeStateMachine() {
		stateMachine = new MenuStateMachine<PauseMenuScreen>(this, PAUSE_MENU_STATES_CONFIG, mapDialog.getMapStateConfigFile());
		stateMachine.changeToInitialState();
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if (action.equals(ACTION_BACK_TO_GAME) && isEventTypeHandled(type)) {
			backToGame();
			return true;
		}
		
		return super.onAction(action, type, parameters);
	}
	
	@Override
	public void showMenu() {
		super.showMenu();
		
		closeControlsDialog();
		closeMapDialog();
		mapDialog.updateMapConfig(gameScreen);
		
		takeGameSnapshot();
		playMenuSound(SOUND_ENTER_PAUSE_MENU);
		
		stateMachine.changeToInitialState();
	}
	
	@Override
	protected String getInputContextName() {
		return INPUT_CONTEXT_NAME;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		drawBackground();
		drawItemMenu();
		drawMap(delta);
		drawAmmoMenu();
		drawButtons();
		drawBanners();
		batch.end();
		
		drawTexts();
		
		drawControlsDialog();
		drawMapDialog(delta);
		
		debugGridRenderer.render(delta);
	}
	
	private void drawBackground() {
		gameSnapshotSprite.draw(batch);
		background.draw(batch, 100, 100, 980, 600);
	}
	
	private void drawItemMenu() {
		itemMenu.draw(batch, 625, 150, 400, 200);
	}
	
	private void drawMap(float delta) {
		mapAnimation.setSpriteConfig(new AnimationSpriteConfig().setX(670).setY(430).setWidth(80).setHeight(80));
		if (buttonShowMap.hasFocus()) {
			mapAnimation.increaseStateTime(delta);
		}
		else {
			mapAnimation.resetStateTime();
		}
		mapAnimation.drawInMenu(batch);
	}
	
	private void drawAmmoMenu() {
		ammoMenu.draw(batch, 825, 380, 200, 110);
	}
	
	private void drawButtons() {
		buttonBackToGame.draw(batch);
		buttonControls.draw(batch);
		buttonRestart.draw(batch);
		buttonQuit.draw(batch);
		buttonShowMap.draw(batch);
	}
	
	private void drawBanners() {
		headerBanner.draw(batch, 125, 540, 650, 250);
		itemMenuBanner.draw(batch, 640, 260, 200, 150);
		ammoMenuBanner.draw(batch, 800, 465, 250, 150);
		mapBanner.draw(batch, 610, 465, 200, 150);
	}
	
	private void drawControlsDialog() {
		controlsDialog.draw();
	}
	
	private void drawMapDialog(float delta) {
		mapDialog.draw(delta);
	}
	
	private void drawTexts() {
		ammoMenu.drawAmmoTexts();
		
		screenTextWriter.setColor(Color.BLACK);
		
		screenTextWriter.setScale(1.5f);
		screenTextWriter.drawText("Pause Menu", 230, 683);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText("Items", 690, 345);
		
		screenTextWriter.drawText("Ammo", 860, 550);
		
		screenTextWriter.drawText("Map", 670, 550);
		
		screenTextWriter.setScale(0.55f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonShowMap) + "Show Map", 675, 408, 80, Align.center, false);
		
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
		if (stateName.startsWith(STATE_PREFIX_ITEM)) {
			int itemIndex = Integer.parseInt(stateName.substring(STATE_PREFIX_ITEM.length())) - 1;
			itemMenu.setHoveredIndex(itemIndex);
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
				case "restartGame":
					button = buttonRestart;
					break;
				case "quit":
					button = buttonQuit;
					break;
				case "showMap":
					button = buttonShowMap;
					break;
				case "mapDialogBack":
					mapDialog.setFocusToBackButton();
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
		
		//will be ignored if the state is not known in the map dialog
		mapDialog.setFocusTo(stateName);
	}
	
	private void unfocusAll() {
		buttonBackToGame.setFocused(false);
		buttonControls.setFocused(false);
		buttonRestart.setFocused(false);
		buttonQuit.setFocused(false);
		buttonShowMap.setFocused(false);
		itemMenu.setHoveredIndex(-1);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		controlsDialog.dispose();
		mapDialog.dispose();
	}
	
	//****************************************************************
	//*** State machine methods (called via reflection)
	//****************************************************************
	
	public void showControls() {
		Gdx.app.debug(getClass().getSimpleName(), "'Show Controlls' selected");
		controlsDialog.setVisible(true);
		stateMachine.changeState("button_controlsDialogBack");
	}
	
	public void showMap() {
		Gdx.app.debug(getClass().getSimpleName(), "'Show Map' selected");
		mapDialog.setVisible(true);
		mapDialog.setFocusToBackButton();
		stateMachine.changeState("button_mapDialogBack");
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
	
	public void selectFastTravelPoint() {
		mapDialog.selectFastTravelPoint();
	}
}
