package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.text.ScreenTextWriter;
import net.jfabricationgames.gdx.texture.TextureLoader;

public class OnScreenItemRenderer implements Disposable {
	
	private static final String TEXTURE_CONFIG = "config/hud/on_screen_item_renderer/textures.json";
	private static final float TEXT_SCALE = 0.9f;
	
	private OrthographicCamera camera;
	private StatsCharacter character;
	private Vector2 tileUpperRight;
	
	private SpriteBatch batch;
	private ScreenTextWriter screenTextWriter;
	
	private TextureRegion coinIcon;
	private TextureRegion keyIcon;
	
	public OnScreenItemRenderer(HeadsUpDisplay hud) {
		this.camera = hud.getCamera();
		this.character = hud.getCharacter();
		batch = new SpriteBatch();
		screenTextWriter = new ScreenTextWriter();
		screenTextWriter.setFont(HeadsUpDisplay.DEFAULT_FONT_NAME);
		tileUpperRight = new Vector2(hud.getHudSceneWidth() - 20f, hud.getHudSceneHeight() - 20f);
		
		loadIcons();
	}
	
	private void loadIcons() {
		TextureLoader textureLoader = new TextureLoader(TEXTURE_CONFIG);
		coinIcon = textureLoader.loadTexture("coin");
		keyIcon = textureLoader.loadTexture("key");
	}
	
	public void render(float delta) {
		batch.setProjectionMatrix(camera.combined);
		
		drawIcons();
		drawText();
	}
	
	private void drawIcons() {
		batch.begin();
		batch.draw(coinIcon, tileUpperRight.x - 40f, tileUpperRight.y - 130f, 35f, 35f);
		batch.draw(keyIcon, tileUpperRight.x - 50f, tileUpperRight.y - 185f, 55f, 55f);
		batch.end();
	}
	
	private void drawText() {
		int coins = character.getCoinsForHud();
		int keys = character.getNormalKeys();
		
		screenTextWriter.setScale(TEXT_SCALE);
		screenTextWriter.setColor(Color.BLACK);
		screenTextWriter.drawText(Integer.toString(coins), tileUpperRight.x - 220f, tileUpperRight.y - 118f, 100, Align.right, false);
		screenTextWriter.drawText(Integer.toString(keys), tileUpperRight.x - 220f, tileUpperRight.y - 163f, 100, Align.right, false);
	}
	
	@Override
	public void dispose() {
		batch.dispose();
	}
}
