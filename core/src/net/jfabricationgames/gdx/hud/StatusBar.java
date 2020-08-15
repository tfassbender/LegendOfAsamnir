package net.jfabricationgames.gdx.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class StatusBar implements Disposable {
	
	private final float healthBarHeightPercent = 0.55f;
	private final float manaBarHeightPercent = 0.3f;
	private final Vector2 size = new Vector2(-400, -80); //negative values because it's drawn from the top right
	private final Vector2 healthBarUpperRightOffset = new Vector2(-10f, -10f);
	private final Vector2 healthBarSize = new Vector2(size.x - healthBarUpperRightOffset.x * 2,
			(size.y - (healthBarUpperRightOffset.y * 4)) * healthBarHeightPercent);
	private final Vector2 manaBarUpperRightOffset = new Vector2(-10f, -10f + healthBarUpperRightOffset.y + healthBarSize.y);
	private final Vector2 manaBarSize = new Vector2(size.x - manaBarUpperRightOffset.x * 2,
			(size.y - (healthBarUpperRightOffset.y * 4)) * manaBarHeightPercent);
	private final Vector2 enduranceBarUpperRightOffset = new Vector2(-10f, -10f + 2 * healthBarUpperRightOffset.y + healthBarSize.y + manaBarSize.y);
	private final Vector2 enduranceBarSize = new Vector2(size.x - manaBarUpperRightOffset.x * 2,
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
	
	private Vector2 tileUpperRight;
	private Color[] healthBarColors;
	
	private float health;
	private float mana;
	private float endurance;
	
	public StatusBar(HeadsUpDisplay hud) {
		this.character = hud.getCharacter();
		this.camera = hud.getCamera();
		shapeRenderer = new ShapeRenderer();
		
		tileUpperRight = new Vector2(hud.getHudSceneWidth() - 20f, hud.getHudSceneHeight() - 20f);
	}
	
	public void render(float delta) {
		queryStats();
		chooseHealthBarColors();
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		drawStatusBar();
		shapeRenderer.end();
	}
	
	private void queryStats() {
		health = character.getHealth();
		mana = character.getMana();
		endurance = character.getEndurance();
	}
	
	private void chooseHealthBarColors() {
		if (health > 0.6) {
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
		shapeRenderer.rect(tileUpperRight.x, tileUpperRight.y, size.x, size.y, backgroundTileColors[0], backgroundTileColors[1],
				backgroundTileColors[2], backgroundTileColors[3]);
		
		//health bar
		shapeRenderer.rect(tileUpperRight.x + healthBarUpperRightOffset.x, tileUpperRight.y + healthBarUpperRightOffset.y, healthBarSize.x,
				healthBarSize.y, barBackgroundColors[0], barBackgroundColors[1], barBackgroundColors[2], barBackgroundColors[3]);
		shapeRenderer.rect(tileUpperRight.x + healthBarUpperRightOffset.x, tileUpperRight.y + healthBarUpperRightOffset.y, healthBarSize.x * health,
				healthBarSize.y, healthBarColors[0], healthBarColors[1], healthBarColors[2], healthBarColors[3]);
		
		//mana bar
		shapeRenderer.rect(tileUpperRight.x + manaBarUpperRightOffset.x, tileUpperRight.y + manaBarUpperRightOffset.y, manaBarSize.x, manaBarSize.y,
				barBackgroundColors[0], barBackgroundColors[1], barBackgroundColors[2], barBackgroundColors[3]);
		shapeRenderer.rect(tileUpperRight.x + manaBarUpperRightOffset.x, tileUpperRight.y + manaBarUpperRightOffset.y, manaBarSize.x * mana,
				manaBarSize.y, manaBarColors[0], manaBarColors[1], manaBarColors[2], manaBarColors[3]);
		
		//endurance bar
		shapeRenderer.rect(tileUpperRight.x + enduranceBarUpperRightOffset.x, tileUpperRight.y + enduranceBarUpperRightOffset.y, enduranceBarSize.x,
				enduranceBarSize.y, barBackgroundColors[0], barBackgroundColors[1], barBackgroundColors[2], barBackgroundColors[3]);
		shapeRenderer.rect(tileUpperRight.x + enduranceBarUpperRightOffset.x, tileUpperRight.y + enduranceBarUpperRightOffset.y,
				enduranceBarSize.x * endurance, enduranceBarSize.y, enduranceBarColors[0], enduranceBarColors[1], enduranceBarColors[2],
				enduranceBarColors[3]);
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
	}
}
