package net.jfabricationgames.gdx.hud.implementation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

import net.jfabricationgames.gdx.constants.Constants;
import net.jfabricationgames.gdx.hud.StatsCharacter;
import net.jfabricationgames.gdx.map.GameMapManager;
import net.jfabricationgames.gdx.text.ScreenTextWriter;
import net.jfabricationgames.gdx.texture.TextureLoader;

public class OnScreenItemRenderer implements Disposable {
	
	private static final String CHARACTER_ACTION_BOW = "BOW";
	private static final String CHARACTER_ACTION_BOMB = "BOMB";
	
	private static final String ITEM_AMMO_TYPE_ARROW = "ARROW";
	private static final String ITEM_AMMO_TYPE_BOMB = "BOMB";
	
	private static final String TEXTURE_CONFIG = "config/hud/on_screen_item_renderer/textures.json";
	private static final float TEXT_SCALE = 0.9f;
	
	private OrthographicCamera camera;
	private StatsCharacter character;
	private Vector2 tileUpperRight;
	
	private SpriteBatch batch;
	private ScreenTextWriter screenTextWriter;
	
	private TextureRegion coinIcon;
	private TextureRegion keyIcon;
	private ObjectMap<String, TextureRegion> specialActionIcons;
	
	public OnScreenItemRenderer(OrthographicCamera camera, StatsCharacter character, float sceneWidth, float sceneHeight) {
		this.camera = camera;
		this.character = character;
		batch = new SpriteBatch();
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(Constants.DEFAULT_FONT_NAME);
		tileUpperRight = new Vector2(sceneWidth - 20f, sceneHeight - 20f);
		
		loadIcons();
	}
	
	private void loadIcons() {
		TextureLoader textureLoader = new TextureLoader(TEXTURE_CONFIG);
		coinIcon = textureLoader.loadTexture("coin");
		keyIcon = textureLoader.loadTexture("key");
		
		specialActionIcons = new ObjectMap<>();
		for (String specialAction : character.getActionList()) {
			specialActionIcons.put(specialAction, textureLoader.loadTexture(specialAction));
		}
	}
	
	public void render(float delta) {
		batch.setProjectionMatrix(camera.combined);
		
		drawIcons();
		drawText();
	}
	
	private void drawIcons() {
		TextureRegion activeSpecialActionIcon = specialActionIcons.get(character.getActiveAction().toLowerCase());
		
		batch.begin();
		batch.draw(coinIcon, tileUpperRight.x - 40f, tileUpperRight.y - 130f, 35f, 35f);
		batch.draw(keyIcon, tileUpperRight.x - 50f, tileUpperRight.y - 185f, 55f, 55f);
		batch.draw(activeSpecialActionIcon, tileUpperRight.x - 40f, tileUpperRight.y - 225f, 35f, 35f);
		batch.end();
	}
	
	private void drawText() {
		int coins = character.getCoinsForHud();
		int keys = character.getNormalKeys();
		int ammo;
		
		switch (character.getActiveAction()) {
			case CHARACTER_ACTION_BOW:
				ammo = character.getAmmo(ITEM_AMMO_TYPE_ARROW);
				break;
			case CHARACTER_ACTION_BOMB:
				ammo = character.getAmmo(ITEM_AMMO_TYPE_BOMB);
				break;
			default:
				ammo = -1;
				break;
		}
		
		chooseTextColor();
		screenTextWriter.setScale(TEXT_SCALE);
		screenTextWriter.drawText(Integer.toString(coins), tileUpperRight.x - 220f, tileUpperRight.y - 118f, 100, Align.right, false);
		screenTextWriter.drawText(Integer.toString(keys), tileUpperRight.x - 220f, tileUpperRight.y - 163f, 100, Align.right, false);
		if (ammo > -1) {
			screenTextWriter.drawText(Integer.toString(ammo), tileUpperRight.x - 220f, tileUpperRight.y - 210f, 100, Align.right, false);
		}
	}
	
	private void chooseTextColor() {
		if (GameMapManager.getInstance().getMap().isDungeonMap() || GameMapManager.getInstance().getMap().isBuildingMap()) {
			screenTextWriter.setColor(Color.GRAY);
		}
		else {
			screenTextWriter.setColor(Color.BLACK);
		}
	}
	
	@Override
	public void dispose() {
		batch.dispose();
	}
}
