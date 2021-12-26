package net.jfabricationgames.gdx.screen.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.jfabricationgames.gdx.data.handler.GlobalValuesDataHandler;
import net.jfabricationgames.gdx.event.EventConfig;
import net.jfabricationgames.gdx.event.EventHandler;
import net.jfabricationgames.gdx.event.EventType;
import net.jfabricationgames.gdx.rune.RuneType;
import net.jfabricationgames.gdx.screen.menu.components.AmmoSubMenu;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton;
import net.jfabricationgames.gdx.screen.menu.components.FocusButton.FocusButtonBuilder;
import net.jfabricationgames.gdx.screen.menu.components.ItemSubMenu;
import net.jfabricationgames.gdx.screen.menu.components.MenuBox;

public class ShopMenuScreen extends InGameMenuScreen<ShopMenuScreen> {
	
	private static final String SHOP_MENU_STATES_CONFIG = "config/menu/shop_menu_states.json";
	private static final String DEFAULT_BUYABLE_ITEMS = "config/shop/default.json";
	private static final String SOUND_BUY_ITEM = "buy";
	private static final String INPUT_CONTEXT_NAME = "shopMenu";
	
	private static final String ACTION_BACK_TO_GAME = "backToGame";
	private static final String ACTION_BACK_TO_GAME_INTERACT_BUTTON = "backToGame_interactButton";
	private static final String STATE_PREFIX_ITEM = "item_";
	private static final String STATE_PREFIX_BUTTON = "button_";
	
	private static final String RUNE_NEEDED_EVENT_KEY = "rune_needed__gebo";
	
	private Array<ItemConfig> items;
	
	private MenuBox background;
	private MenuBox headerBanner;
	private MenuBox descriptionBox;
	private ItemSubMenu itemMenu;
	private MenuBox itemMenuBanner;
	private AmmoSubMenu ammoMenu;
	private MenuBox ammoMenuBanner;
	private FocusButton buttonBackToGame;
	
	public ShopMenuScreen(MenuGameScreen gameScreen) {
		super(gameScreen, SHOP_MENU_STATES_CONFIG);
		
		initialize();
	}
	
	private void initialize() {
		itemMenu = new ItemSubMenu(4, 2); // needs to be created before loading the item config
		loadBuyableItemConfig(DEFAULT_BUYABLE_ITEMS);
		createComponents();
		
		stateMachine.changeToInitialState();
	}
	
	@SuppressWarnings("unchecked")
	public void loadBuyableItemConfig(String configPath) {
		if (configPath == null || configPath.isEmpty()) {
			Gdx.app.debug(getClass().getSimpleName(), "Config path is empty. New config will not be loaded");
			return;
		}
		
		Gdx.app.debug(getClass().getSimpleName(), "Loading shop menu items config from path: " + configPath);
		
		Json json = new Json();
		FileHandle configFile = Gdx.files.internal(configPath);
		items = json.fromJson(Array.class, ItemConfig.class, configFile);
		
		Array<String> displayedItems = new Array<>(items.size);
		for (ItemConfig config : items) {
			displayedItems.add(config.technicalName);
		}
		itemMenu.setDisplayedItems(displayedItems);
	}
	
	private void createComponents() {
		background = new MenuBox(8, 6, MenuBox.TextureType.GREEN_BOARD);
		headerBanner = new MenuBox(6, 2, MenuBox.TextureType.BIG_BANNER);
		descriptionBox = new MenuBox(5, 4, MenuBox.TextureType.YELLOW_PAPER);
		
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
		if (items.size > selectedItemIndex) {
			ItemConfig item = items.get(selectedItemIndex);
			String itemName = item.technicalName;
			int itemCosts = item.cost;
			
			if (itemName != null && itemCosts > 0) {
				if (itemsRuneCollected()) {
					if (!isItemAlreadyOwned(item)) {
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
						EventHandler.getInstance().fireEvent(new EventConfig().setEventType(EventType.ITEM_BOUGHT_BUT_ALREADY_OWNED));
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
	
	private boolean isItemAlreadyOwned(ItemConfig item) {
		return item.itemOwnedGlobalValueKey != null && !item.itemOwnedGlobalValueKey.isEmpty()
				&& GlobalValuesDataHandler.getInstance().getAsBoolean(item.itemOwnedGlobalValueKey);
	}
	
	private void fireBuyItemEvent(String itemName, int itemCosts) {
		EventHandler eventHandler = EventHandler.getInstance();
		eventHandler.fireEvent(new EventConfig().setEventType(EventType.TAKE_PLAYERS_COINS).setIntValue(itemCosts));
		eventHandler.fireEvent(new EventConfig().setEventType(EventType.PLAYER_BUY_ITEM).setStringValue(itemName));
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
		
		setProjectionMatrixBeforeRendering();
		
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
		screenTextWriter.drawText(getButtonTextColorEncoding(buttonBackToGame) + "Leave Shop", buttonTextX, 296, buttonTextWidth, Align.center,
				false);
		
		screenTextWriter.setScale(0.7f);
		String itemName = "";
		int itemCosts = 0;
		int playersCoins = player.getCoins();
		
		int selectedItemIndex = itemMenu.getHoveredIndex();
		if (selectedItemIndex >= 0 && items.size > selectedItemIndex) {
			itemName = items.get(selectedItemIndex).displayName;
			itemCosts = items.get(selectedItemIndex).cost;
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
	
	public static class ItemConfig {
		
		public String displayName;
		public String technicalName;
		public int cost;
		public String itemOwnedGlobalValueKey;
	}
}
