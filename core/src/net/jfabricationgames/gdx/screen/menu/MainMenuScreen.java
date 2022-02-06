package net.jfabricationgames.gdx.screen.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;

import net.jfabricationgames.gdx.character.player.implementation.SpecialAction;
import net.jfabricationgames.gdx.data.GameDataHandler;
import net.jfabricationgames.gdx.data.GameDataService;
import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.screen.game.GameScreen;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screen.menu.components.MainMenuAnimation;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;
import net.jfabricationgames.gdx.screen.menu.dialog.LoadGameDialog;

public class MainMenuScreen extends MenuScreen<MainMenuScreen> {
	
	private static final String INPUT_CONTEXT_NAME = "mainMenu";
	private static final String MAIN_MENU_STATE_CONFIG = "config/menu/main_menu_states.json";
	private static final String STATE_PREFIX_LOAD_DIALOG = "loadDialog_";
	
	private MenuBox background;
	private MenuBox bannerMainMenu;
	private MenuBox banner;
	private FocusButton buttonContinueGame;
	private FocusButton buttonLoadGame;
	private FocusButton buttonStartNewGame;
	private FocusButton buttonQuit;
	
	private LoadGameDialog loadGameDialog;
	
	private MainMenuAnimation mainMenuAnimation;
	
	public MainMenuScreen() {
		super(MAIN_MENU_STATE_CONFIG);
		
		createComponents();
		createDialogs();
		stateMachine.changeToInitialState();
		
		mainMenuAnimation = new MainMenuAnimation();
		
		setInputContext();
		
		//adapt the camera position to center the menu on the screen (instead of changing all components)
		camera.position.y -= 20;
	}
	
	private void createComponents() {
		background = new MenuBox(12, 15, MenuBox.TextureType.GREEN_BOARD);
		banner = new MenuBox(10, 2, MenuBox.TextureType.BIG_BANNER);
		bannerMainMenu = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER_LOW);
		
		buttonContinueGame = createButton(3);
		buttonLoadGame = createButton(2);
		buttonStartNewGame = createButton(1);
		buttonQuit = createButton(0);
		
		buttonContinueGame.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonLoadGame.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonStartNewGame.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonQuit.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
	}
	
	private void createDialogs() {
		loadGameDialog = new LoadGameDialog(camera, () -> {
		}, this::playMenuSound);
	}
	
	private FocusButton createButton(int button) {
		int buttonWidth = 290;
		int buttonHeight = 55;
		int buttonPosX = 370;
		int lowestButtonY = 90;
		int buttonGapY = 50;
		
		return new FocusButtonBuilder() //
				.setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG) //
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED) //
				.setSize(buttonWidth, buttonHeight) //
				.setPosition(buttonPosX, lowestButtonY + button * (buttonHeight + buttonGapY)) //
				.build();
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
		mainMenuAnimation.drawAnimation(batch, delta);
		drawBackground();
		drawButtons();
		drawBanners();
		batch.end();
		
		drawTexts();
		
		drawLoadGameDialog();
	}
	
	private void drawBackground() {
		background.draw(batch, 300, 30, 580, 600);
	}
	
	private void drawButtons() {
		buttonContinueGame.draw(batch);
		buttonLoadGame.draw(batch);
		buttonStartNewGame.draw(batch);
		buttonQuit.draw(batch);
	}
	
	private void drawBanners() {
		banner.draw(batch, -15, 520, 1200, 350);
		bannerMainMenu.draw(batch, 310, 430, 550, 250);
	}
	
	private void drawTexts() {
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(2f);
		screenTextWriter.drawText("Legend of Asamnir", 150, 720);
		
		int buttonTextX = 370;
		int buttonTextWidth = 430;
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.5f);
		screenTextWriter.drawText("Main Menu", buttonTextX + 10, 573, buttonTextWidth, Align.center, false);
		
		screenTextWriter.setScale(1.15f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonContinueGame) + "Continue", buttonTextX, 462, buttonTextWidth, Align.center,
				false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonLoadGame) + "Load Game", buttonTextX, 356, buttonTextWidth, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonStartNewGame) + "New Game", buttonTextX, 252, buttonTextWidth, Align.center,
				false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonQuit) + "Quit", buttonTextX, 148, buttonTextWidth, Align.center, false);
	}
	
	private String getButtonTextColorEncoding(FocusButton button) {
		return button.hasFocus() ? TEXT_COLOR_ENCODING_FOCUS : TEXT_COLOR_ENCODING_NORMAL;
	}
	
	private void drawLoadGameDialog() {
		loadGameDialog.draw();
	}
	
	@Override
	public void setFocusTo(String stateName, String leavingState) {
		unfocusAll();
		
		if (stateName.startsWith(STATE_PREFIX_LOAD_DIALOG)) {
			loadGameDialog.setFocusTo(stateName);
		}
		else {
			switch (stateName) {
				case "button_continue":
					buttonContinueGame.setFocused(true);
					break;
				case "button_load":
					buttonLoadGame.setFocused(true);
					break;
				case "button_startGame":
					buttonStartNewGame.setFocused(true);
					break;
				case "button_quit":
					buttonQuit.setFocused(true);
					break;
			}
		}
	}
	
	private void unfocusAll() {
		buttonContinueGame.setFocused(false);
		buttonLoadGame.setFocused(false);
		buttonStartNewGame.setFocused(false);
		buttonQuit.setFocused(false);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		loadGameDialog.dispose();
	}
	
	private void createGameScreen() {
		createGameScreen(null);
	}
	
	private void createGameScreen(Runnable afterCreatingGameScreen) {
		Gdx.app.log(getClass().getSimpleName(), "Crating game screen");
		GameScreen.loadAndShowGameScreen(afterCreatingGameScreen);
		dispose();
	}
	
	//****************************************************************
	//*** State machine methods (called via reflection)
	//****************************************************************
	
	public void continueGame() {
		new GameDataService().loadGameDataFromQuicksaveSlot();
		createGameScreen();
	}
	
	public void showLoadGameMenu() {
		loadGameDialog.setVisible(true);
		stateMachine.changeState("loadDialog_button_loadGameDialogBack");
	}
	
	public void startGame() {
		GameDataHandler.getInstance().createNewGameData();
		createGameScreen(() -> {
			GlobalValuesDataHandler.getInstance().put(SpecialAction.JUMP.actionEnabledGlobalValueKey, true);
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.NEW_GAME_STARTED));
		});
	}
	
	//*********************************************************************
	//*** State machine methods for load dialog (called via reflection)
	//*********************************************************************
	
	public void closeLoadGameDialog() {
		loadGameDialog.setVisible(false);
		stateMachine.changeState("button_load");
	}
	
	public void loadFromQuickSaveSlot() {
		loadGameDialog.loadFromQuickSaveSlot();
		createGameScreen();
	}
	
	public void loadFromSlot1() {
		loadGameDialog.loadFromSlot(1);
		createGameScreen();
	}
	
	public void loadFromSlot2() {
		loadGameDialog.loadFromSlot(2);
		createGameScreen();
	}
	
	public void loadFromSlot3() {
		loadGameDialog.loadFromSlot(3);
		createGameScreen();
	}
	
	public void loadFromSlot4() {
		loadGameDialog.loadFromSlot(4);
		createGameScreen();
	}
	
	public void loadFromSlot5() {
		loadGameDialog.loadFromSlot(5);
		createGameScreen();
	}
}
