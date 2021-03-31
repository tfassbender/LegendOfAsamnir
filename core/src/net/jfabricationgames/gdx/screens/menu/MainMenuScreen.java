package net.jfabricationgames.gdx.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;

import net.jfabricationgames.gdx.DwarfScrollerGame;
import net.jfabricationgames.gdx.data.GameDataHandler;
import net.jfabricationgames.gdx.data.GameDataService;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;
import net.jfabricationgames.gdx.screens.menu.dialog.LoadGameDialog;

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
	
	public MainMenuScreen() {
		super(MAIN_MENU_STATE_CONFIG);
		
		createComponents();
		createDialogs();
		stateMachine.changeToInitialState();
		
		showMenu();
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
		loadGameDialog = new LoadGameDialog(() -> {}, this::playMenuSound);
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
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		drawBackground();
		drawButtons();
		drawBanners();
		batch.end();
		
		drawTexts();

		drawLoadGameDialog();
	}
	
	@Override
	protected String getInputContextName() {
		return INPUT_CONTEXT_NAME;
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
		banner.draw(batch, 0, 520, 1200, 350);
		bannerMainMenu.draw(batch, 315, 430, 550, 250);
	}
	
	private void drawTexts() {
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(2f);
		screenTextWriter.drawText("Dwarf Scroller GDX", 110, 720);
		
		int buttonTextX = 370;
		int buttonTextWidth = 430;
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.setScale(1.5f);
		screenTextWriter.drawText("Main Menu", buttonTextX + 5, 573, buttonTextWidth, Align.center, false);
		
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
	protected void setFocusTo(String stateName, String leavingState) {
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
		GameDataHandler.getInstance().createNewGameData();
		DwarfScrollerGame.getInstance().setScreen(new GameScreen());
	}
	
	//****************************************************************
	//*** State machine methods (called via reflection)
	//****************************************************************
	
	public void continueGame() {
		createGameScreen();
		new GameDataService().loadGameDataFromQuicksaveSlot();
		dispose();
	}
	
	public void showLoadGameMenu() {
		loadGameDialog.setVisible(true);
		stateMachine.changeState("loadDialog_button_loadGameDialogBack");
	}
	
	public void startGame() {
		GameDataHandler.getInstance().createNewGameData();
		createGameScreen();
		dispose();
	}
	
	//*********************************************************************
	//*** State machine methods for load dialog (called via reflection)
	//*********************************************************************

	public void closeLoadGameDialog() {
		loadGameDialog.setVisible(false);
		stateMachine.changeState("button_load");
	}
	
	public void loadFromQuickSaveSlot() {
		createGameScreen();
		loadGameDialog.loadFromQuickSaveSlot();
	}
	
	public void loadFromSlot1() {
		createGameScreen();
		loadGameDialog.loadFromSlot(1);
	}
	
	public void loadFromSlot2() {
		createGameScreen();
		loadGameDialog.loadFromSlot(2);
	}
	
	public void loadFromSlot3() {
		createGameScreen();
		loadGameDialog.loadFromSlot(3);
	}
	
	public void loadFromSlot4() {
		createGameScreen();
		loadGameDialog.loadFromSlot(4);
	}
	
	public void loadFromSlot5() {
		createGameScreen();
		loadGameDialog.loadFromSlot(5);
	}
}
