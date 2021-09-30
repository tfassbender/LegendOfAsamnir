package net.jfabricationgames.gdx.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.item.Item;
import net.jfabricationgames.gdx.item.ItemFactory;
import net.jfabricationgames.gdx.item.rune.RuneType;
import net.jfabricationgames.gdx.screens.game.GameScreen;
import net.jfabricationgames.gdx.screens.menu.components.AmmoSubMenu;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton;
import net.jfabricationgames.gdx.screens.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screens.menu.components.ItemSubMenu;
import net.jfabricationgames.gdx.screens.menu.components.MenuBox;
import net.jfabricationgames.gdx.screens.menu.components.ShopItemSubMenu;

public class ShopMenuScreen extends InGameMenuScreen<ShopMenuScreen> {
	
	private static final String SHOP_MENU_STATES_CONFIG = "config/menu/shop_menu_states.json";
	private static final String SOUND_BUY_ITEM = "buy";
	private static final String INPUT_CONTEXT_NAME = "shopMenu";
	
	private static final String ACTION_BACK_TO_GAME = "backToGame";
	private static final String ACTION_BACK_TO_GAME_INTERACT_BUTTON = "backToGame_interactButton";
	private static final String STATE_PREFIX_ITEM = "item_";
	private static final String STATE_PREFIX_BUTTON = "button_";
	
	private static final String RUNE_NEEDED_EVENT_KEY = "rune_needed__gebo";
	
	private static final Array<String> ITEM_NAMES = new Array<>(new String[] {"health", "shield", "mead", null, "arrow", "bomb"});
	private static final Array<Integer> ITEM_COSTS = new Array<>(new Integer[] {30, 20, 15, 0, 25, 25});
	
	private MenuBox background;
	private MenuBox headerBanner;
	private MenuBox descriptionBox;
	private ItemSubMenu itemMenu;
	private MenuBox itemMenuBanner;
	private AmmoSubMenu ammoMenu;
	private MenuBox ammoMenuBanner;
	private FocusButton buttonBackToGame;
	
	public ShopMenuScreen(GameScreen gameScreen) {
		super(gameScreen, SHOP_MENU_STATES_CONFIG);
		
		initialize();
	}
	
	private void initialize() {
		createComponents();
		
		stateMachine.changeToInitialState();
	}
	
	private void createComponents() {
		background = new MenuBox(8, 6, MenuBox.TextureType.GREEN_BOARD);
		headerBanner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		descriptionBox = new MenuBox(5, 4, MenuBox.TextureType.YELLOW_PAPER);
		
		itemMenu = new ShopItemSubMenu();
		itemMenuBanner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER);
		
		int buttonWidth = 290;
		int buttonHeight = 55;
		int buttonPosX = 290;
		int lowestButtonY = 240;
		buttonBackToGame = new FocusButtonBuilder().setNinePatchConfig(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG)
				.setNinePatchConfigFocused(FocusButton.BUTTON_GREEN_NINEPATCH_CONFIG_FOCUSED).setSize(buttonWidth, buttonHeight)
				.setPosition(buttonPosX, lowestButtonY).build();
		
		buttonBackToGame.scaleBy(FocusButton.DEFAULT_BUTTON_SCALE);
		
		ammoMenu = new AmmoSubMenu(player);
		ammoMenuBanner = new MenuBox(4, 2, MenuBox.TextureType.BIG_BANNER_LOW);
	}
	
	@Override
	public boolean onAction(String action, Type type, Parameters parameters) {
		if ((action.equals(ACTION_BACK_TO_GAME) || action.equals(ACTION_BACK_TO_GAME_INTERACT_BUTTON)) && isEventTypeHandled(type)) {
			backToGame();
			return true;
		}
		
		return super.onAction(action, type, parameters);
	}
	
	/**
	 * Called from the MenuStateMachine via reflection.
	 */
	public void selectCurrentItem() {
		int selectedItemIndex = itemMenu.getHoveredIndex();
		if (ITEM_NAMES.size > selectedItemIndex) {
			String itemName = ITEM_NAMES.get(selectedItemIndex);
			int itemCosts = ITEM_COSTS.get(selectedItemIndex);
			
			if (itemName != null && itemCosts > 0) {
				if (itemsRuneCollected()) {
					if (player.getCoins() >= itemCosts) {
						playMenuSound(SOUND_BUY_ITEM);
						fireBuyItemEvent(itemName, itemCosts);
					}
					else {
						playMenuSound(InGameMenuScreen.SOUND_ERROR);
					}
				}
				else {
					backToGame();
					EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.RUNE_NEEDED).setStringValue(RUNE_NEEDED_EVENT_KEY));
				}
			}
		}
	}
	
	private boolean itemsRuneCollected() {
		return RuneType.GEBO.isCollected();
	}
	
	private void fireBuyItemEvent(String itemName, int itemCosts) {
		Item boughtItem = ItemFactory.createItem(itemName, 0, 0, new MapProperties());
		
		EventHandler eventHandler = EventHandler.getInstance();
		eventHandler.fireEvent(new EventConfig().setEventType(EventType.TAKE_PLAYERS_COINS).setIntValue(itemCosts));
		eventHandler.fireEvent(new EventConfig().setEventType(EventType.PLAYER_BUY_ITEM).setParameterObject(boughtItem));
	}
	
	@Override
	public void showMenu() {
		super.showMenu();
		takeGameSnapshot();
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
		drawAmmoMenu();
		drawDescriptionBox();
		drawButtons();
		drawBanners();
		batch.end();
		
		drawTexts();
	}
	
	private void drawBackground() {
		gameSnapshotSprite.draw(batch);
		background.draw(batch, 225, 175, 810, 500);
	}
	
	private void drawItemMenu() {
		itemMenu.draw(batch, 290, 370, 400, 200);
	}
	
	private void drawAmmoMenu() {
		ammoMenu.draw(batch, 770, 230, 200, 110);
	}
	
	private void drawDescriptionBox() {
		descriptionBox.draw(batch, 695, 355, 300, 270);
	}
	
	private void drawButtons() {
		buttonBackToGame.draw(batch);
	}
	
	private void drawBanners() {
		headerBanner.draw(batch, 225, 520, 650, 250);
		itemMenuBanner.draw(batch, 310, 480, 200, 150);
		ammoMenuBanner.draw(batch, 745, 270, 250, 150);
	}
	
	private void drawTexts() {
		ammoMenu.drawAmmoTexts();
		
		screenTextWriter.setColor(Color.BLACK);
		
		screenTextWriter.setScale(1.5f);
		screenTextWriter.drawText("Item Shop", 380, 663);
		
		screenTextWriter.setScale(0.8f);
		screenTextWriter.drawText("Items", 360, 565);
		
		screenTextWriter.drawText("Ammo", 807, 355);
		
		screenTextWriter.setScale(1.15f);
		int buttonTextX = 290;
		int buttonTextWidth = 430;
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToGame) + "Back to Game", buttonTextX, 300, buttonTextWidth, Align.center,
				false);
		
		screenTextWriter.setScale(0.7f);
		String itemName = "";
		int itemCosts = 0;
		int playersCoins = player.getCoins();
		
		int selectedItemIndex = itemMenu.getHoveredIndex();
		if (selectedItemIndex >= 0 && ITEM_NAMES.size > selectedItemIndex) {
			itemName = ITEM_NAMES.get(selectedItemIndex);
			itemCosts = ITEM_COSTS.get(selectedItemIndex);
		}
		if (itemName == null || itemName.isEmpty()) {
			itemName = "---";
		}
		
		String textColor = "[#EB5000]";
		screenTextWriter.drawText("Item:\n" + textColor + itemName + "\n[BLACK]Costs:\n" + textColor + itemCosts + " Coins\n[BLACK]You have:\n"
				+ textColor + playersCoins + " Coins", 745, 573);
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
		else if (stateName.startsWith(STATE_PREFIX_BUTTON)) {
			String buttonId = stateName.substring(STATE_PREFIX_BUTTON.length());
			FocusButton button = null;
			switch (buttonId) {
				case "backToGame":
					button = buttonBackToGame;
					break;
				default:
					throw new IllegalStateException("Unexpected button state identifier: " + STATE_PREFIX_BUTTON + buttonId);
			}
			if (button != null) {
				button.setFocused(true);
			}
		}
	}
	
	private void unfocusAll() {
		buttonBackToGame.setFocused(false);
		itemMenu.setHoveredIndex(-1);
	}
}
