package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.jfabricationgames.gdx.texture.TextureLoader;

public class StatusBar implements Disposable {
	
	private static final String TEXTURE_CONFIG = "config/hud/status_bar/textures.json";
	
	private final float healthBarHeightPercent = 0.55f;
	private final float armorBarHeightPercent = 0.25f;//takes a part of the health bar
	private final float manaBarHeightPercent = 0.3f;
	private final float iconOffsetX = -30f;
	private final Vector2 size = new Vector2(-400, -80); //negative values because it's drawn from the top right
	private final Vector2 healthBarUpperRightOffset = new Vector2(-10f, -10f);
	private final Vector2 healthBarSize = new Vector2(size.x - healthBarUpperRightOffset.x * 2 - iconOffsetX,
			(size.y - (healthBarUpperRightOffset.y * 4)) * healthBarHeightPercent);
	private final Vector2 armorBarUpperRightOffset = new Vector2(-10f, -10f + healthBarSize.y * (1f - armorBarHeightPercent));
	private final Vector2 armorBarSize = new Vector2(healthBarSize.x, healthBarSize.y * armorBarHeightPercent);
	private final Vector2 manaBarUpperRightOffset = new Vector2(-10f, -10f + healthBarUpperRightOffset.y + healthBarSize.y);
	private final Vector2 manaBarSize = new Vector2(size.x - manaBarUpperRightOffset.x * 2 - iconOffsetX,
			(size.y - (healthBarUpperRightOffset.y * 4)) * manaBarHeightPercent);
	private final Vector2 enduranceBarUpperRightOffset = new Vector2(-10f, -10f + 2 * healthBarUpperRightOffset.y + healthBarSize.y + manaBarSize.y);
	private final Vector2 enduranceBarSize = new Vector2(size.x - manaBarUpperRightOffset.x * 2 - iconOffsetX,
			(size.y - (healthBarUpperRightOffset.y * 4)) * (1f - healthBarHeightPercent - manaBarHeightPercent));
	
	private final Color[] backgroundTileColors = new Color[] {//
			new Color(0.3f, 0.3f, 0.3f, 1f), //top-right
			new Color(0.35f, 0.35f, 0.35f, 1f), //top-left
			new Color(0.2f, 0.2f, 0.2f, 1f), //bottom-left
			new Color(0.05f, 0.05f, 0.05f, 1f) //bottom-right
	};
	
	private final Color[] barBackgroundColors = new Color[] {//
			Color.DARK_GRAY, //top-right
			Color.DARK_GRAY, //top-left
			Color.BLACK, //bottom-left
			Color.BLACK //bottom-right
	};
	
	private final Color[] healthBarColorsHigh = new Color[] {//
			new Color(0f, 0.85f, 0f, 1f), //top-right
			Color.GREEN, //top-left
			Color.DARK_GRAY, //bottom-left
			new Color(0.05f, 0.05f, 0.05f, 1f) //bottom-right
	};
	
	private final Color[] healthBarColorsMid = new Color[] {//
			new Color(0.9f, 0.9f, 0.1f, 1f), //top-right
			Color.YELLOW, //top-left
			Color.DARK_GRAY, //bottom-left
			new Color(0.05f, 0.05f, 0.05f, 1f) //bottom-right
	};
	
	private final Color[] healthBarColorsLow = new Color[] {//
			new Color(0.85f, 0f, 0f, 1f), //top-right
			Color.RED, //top-left
			Color.DARK_GRAY, //bottom-left
			new Color(0.05f, 0.05f, 0.05f, 1f) //bottom-right
	};
	
	private final Color[] armorBarColors = new Color[] {//
			Color.LIGHT_GRAY, //top-right
			new Color(0.7f, 0.7f, 0.8f, 1f), //top-left
			Color.GRAY, //bottom-left
			Color.DARK_GRAY, //bottom-right
	};
	
	private final Color[] manaBarColors = new Color[] {//
			new Color(0f, 0f, 0.85f, 1f), //top-right
			Color.BLUE, //top-left
			Color.DARK_GRAY, //bottom-left
			new Color(0.15f, 0.15f, 0.15f, 1f) //bottom-right
	};
	
	private final Color[] enduranceBarColors = new Color[] {//
			new Color(0.9f, 0.9f, 0.1f, 1f), //top-right
			Color.YELLOW, //top-left
			new Color(0.45f, 0.45f, 0.15f, 1f), //bottom-left
			new Color(0.35f, 0.35f, 0.05f, 1f) //bottom-right
	};
	
	private StatsCharacter character;
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	
	private Vector2 tileUpperRight;
	private Color[] healthBarColors;
	
	private float health;
	private float mana;
	private float endurance;
	private float armor;
	
	private TextureRegion healthIcon;
	private TextureRegion armorIcon;
	private TextureRegion manaIcon;
	
	public StatusBar(OrthographicCamera camera, StatsCharacter character, float sceneWidth, float sceneHeight) {
		this.camera = camera;
		this.character = character;
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		tileUpperRight = new Vector2(sceneWidth - 20f, sceneHeight - 20f);
		
		loadIcons();
	}
	
	private void loadIcons() {
		TextureLoader textureLoader = new TextureLoader(TEXTURE_CONFIG);
		healthIcon = textureLoader.loadTexture("health");
		armorIcon = textureLoader.loadTexture("shield");
		manaIcon = textureLoader.loadTexture("mana");
	}
	
	public void render(float delta) {
		queryStats();
		chooseHealthBarColors();
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		batch.setProjectionMatrix(camera.combined);
		
		drawStatusBar();
		drawIcons();
	}
	
	private void queryStats() {
		health = character.getHealth();
		mana = character.getMana();
		endurance = character.getEndurance();
		armor = character.getArmor();
	}
	
	private void chooseHealthBarColors() {
		if (health > 0.4) {
			healthBarColors = healthBarColorsHigh;
		}
		else if (health > 0.2) {
			healthBarColors = healthBarColorsMid;
		}
		else {
			healthBarColors = healthBarColorsLow;
		}
	}
	
	private void drawStatusBar() {
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.rect(tileUpperRight.x, tileUpperRight.y, size.x, size.y, backgroundTileColors[0], backgroundTileColors[1],
				backgroundTileColors[2], backgroundTileColors[3]);
		
		//bar backgrounds
		shapeRenderer.rect(tileUpperRight.x + healthBarUpperRightOffset.x + iconOffsetX, tileUpperRight.y + healthBarUpperRightOffset.y,
				healthBarSize.x, healthBarSize.y, barBackgroundColors[0], barBackgroundColors[1], barBackgroundColors[2], barBackgroundColors[3]);
		shapeRenderer.rect(tileUpperRight.x + manaBarUpperRightOffset.x + iconOffsetX, tileUpperRight.y + manaBarUpperRightOffset.y, manaBarSize.x,
				manaBarSize.y, barBackgroundColors[0], barBackgroundColors[1], barBackgroundColors[2], barBackgroundColors[3]);
		shapeRenderer.rect(tileUpperRight.x + enduranceBarUpperRightOffset.x + iconOffsetX, tileUpperRight.y + enduranceBarUpperRightOffset.y,
				enduranceBarSize.x, enduranceBarSize.y, barBackgroundColors[0], barBackgroundColors[1], barBackgroundColors[2],
				barBackgroundColors[3]);
		
		//health bar
		shapeRenderer.rect(tileUpperRight.x + healthBarUpperRightOffset.x + iconOffsetX, tileUpperRight.y + healthBarUpperRightOffset.y,
				healthBarSize.x * health, healthBarSize.y, healthBarColors[0], healthBarColors[1], healthBarColors[2], healthBarColors[3]);
		
		//armor bar
		shapeRenderer.rect(tileUpperRight.x + armorBarUpperRightOffset.x + iconOffsetX, tileUpperRight.y + armorBarUpperRightOffset.y,
				armorBarSize.x * armor, armorBarSize.y, armorBarColors[0], armorBarColors[1], armorBarColors[2], armorBarColors[3]);
		
		//mana bar
		shapeRenderer.rect(tileUpperRight.x + manaBarUpperRightOffset.x + iconOffsetX, tileUpperRight.y + manaBarUpperRightOffset.y,
				manaBarSize.x * mana, manaBarSize.y, manaBarColors[0], manaBarColors[1], manaBarColors[2], manaBarColors[3]);
		
		//endurance bar
		shapeRenderer.rect(tileUpperRight.x + enduranceBarUpperRightOffset.x + iconOffsetX, tileUpperRight.y + enduranceBarUpperRightOffset.y,
				enduranceBarSize.x * endurance, enduranceBarSize.y, enduranceBarColors[0], enduranceBarColors[1], enduranceBarColors[2],
				enduranceBarColors[3]);
		
		shapeRenderer.end();
	}
	
	private void drawIcons() {
		batch.begin();
		batch.draw(healthIcon, tileUpperRight.x - 30f, tileUpperRight.y - 30f, 25f, 25f);
		batch.draw(armorIcon, tileUpperRight.x - 40f, tileUpperRight.y - 40f, 25f, 25f);
		batch.draw(manaIcon, tileUpperRight.x - 30f, tileUpperRight.y - 55f, 20f, 20f);
		batch.end();
	}
	
	public void getPosition() {
		Vector2 position = new Vector2(tileUpperRight);
		position.x -= size.x;
		position.y -= size.y;
	}
	public void setPosition(float x, float y) {
		tileUpperRight.x = x + size.x;
		tileUpperRight.y = y + size.y;
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
		batch.dispose();
	}
}
