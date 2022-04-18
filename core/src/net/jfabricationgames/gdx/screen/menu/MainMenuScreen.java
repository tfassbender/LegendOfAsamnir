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
import net.jfabricationgames.gdx.screen.menu.dialog.CreditsDialog;
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
	private FocusButton buttonCredits;
	private FocusButton buttonQuit;
	
	private LoadGameDialog loadGameDialog;
	private CreditsDialog creditsDialog;
	
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
		
		buttonContinueGame = createButton(4);
		buttonLoadGame = createButton(3);
		buttonStartNewGame = createButton(2);
		buttonCredits = createButton(1);
		buttonQuit = createButton(0);
		
		buttonContinueGame.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonLoadGame.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonStartNewGame.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonCredits.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		buttonQuit.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
	}
	
	private void createDialogs() {
		loadGameDialog = new LoadGameDialog(camera, () -> {
		}, this::playMenuSound);
		creditsDialog = new CreditsDialog(camera);
	}
	
	private FocusButton createButton(int button) {
		int buttonWidth = 290;
		int buttonHeight = 55;
		int buttonPosX = 370;
		int lowestButtonY = 40;
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
		
		loadGameDialog.draw();
		creditsDialog.draw();
	}
	
	private void drawBackground() {
		background.draw(batch, 300, 0, 580, 650);
	}
	
	private void drawButtons() {
		buttonContinueGame.draw(batch);
		buttonLoadGame.draw(batch);
		buttonStartNewGame.draw(batch);
		buttonCredits.draw(batch);
		buttonQuit.draw(batch);
	}
	
	private void drawBanners() {
		banner.draw(batch, -15, 550, 1200, 350);
		bannerMainMenu.draw(batch, 310, 470, 550, 250);
	}
	
	private void drawTexts() {
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(2f);
		screenTextWriter.drawText("Legend of Asamnir", 150, 750);
		
		int buttonTextX = 370;
		int buttonTextWidth = 430;
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.5f);
		screenTextWriter.drawText("Main Menu", buttonTextX + 10, 613, buttonTextWidth, Align.center, false);
		
		screenTextWriter.setScale(1.15f);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonContinueGame) + "Continue", buttonTextX, 517, buttonTextWidth, Align.center,
				false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonLoadGame) + "Load Game", buttonTextX, 411, buttonTextWidth, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonStartNewGame) + "New Game", buttonTextX, 307, buttonTextWidth, Align.center,
				false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonCredits) + "Credits", buttonTextX, 202, buttonTextWidth, Align.center, false);
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonQuit) + "Quit", buttonTextX, 98, buttonTextWidth, Align.center, false);
	}
	
	private String getButtonTextColorEncoding(FocusButton button) {
		return button.hasFocus() ? TEXT_COLOR_ENCODING_FOCUS : TEXT_COLOR_ENCODING_NORMAL;
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
				case "button_credits":
					buttonCredits.setFocused(true);
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
		buttonCredits.setFocused(false);
		buttonQuit.setFocused(false);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		loadGameDialog.dispose();
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
		try {
			new GameDataService().loadGameDataFromQuicksaveSlot();
			createGameScreen(() -> {
				EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_LOADED));
			});
		}
		catch (IllegalStateException e) {
			Gdx.app.log(getClass().getSimpleName(), "Continue Game not possible due to an exception. Starting new game instead: " + e.getMessage());
			startGame();
		}
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
	
	public void showCreditsDialog() {
		creditsDialog.setVisible(true);
		stateMachine.changeState("creditsDialog_button_back");
	}
	
	public void closeCreditsDialog() {
		creditsDialog.setVisible(false);
		stateMachine.changeState("button_credits");
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
		createGameScreen(() -> {
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_LOADED));
		});
	}
	
	public void loadFromSlot1() {
		loadGameDialog.loadFromSlot(1);
		createGameScreen(() -> {
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_LOADED));
		});
	}
	
	public void loadFromSlot2() {
		loadGameDialog.loadFromSlot(2);
		createGameScreen(() -> {
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_LOADED));
		});
	}
	
	public void loadFromSlot3() {
		loadGameDialog.loadFromSlot(3);
		createGameScreen(() -> {
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_LOADED));
		});
	}
	
	public void loadFromSlot4() {
		loadGameDialog.loadFromSlot(4);
		createGameScreen(() -> {
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_LOADED));
		});
	}
	
	public void loadFromSlot5() {
		loadGameDialog.loadFromSlot(5);
		createGameScreen(() -> {
			EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.GAME_LOADED));
		});
	}
}
