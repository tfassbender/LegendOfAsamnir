package net.jfabricationgames.gdx.character.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.jfabricationgames.gdx.screens.game.GameScreen;

public class EnemyHealthBarRenderer {
	
	private static final int HEALTH_BAR_HEIGHT = 2;
	private static final Color COLOR_HEALTH_HIGH = Color.GREEN;
	private static final Color COLOR_HEALTH_MID = Color.YELLOW;
	private static final Color COLOR_HEALTH_LOW = Color.RED;
	
	public void drawHealthBar(ShapeRenderer shapeRenderer, float health, float x, float y, float width) {
		Color color = getColor(health);
		shapeRenderer.rect(x, y, 0, 0, width * health, HEALTH_BAR_HEIGHT, GameScreen.WORLD_TO_SCREEN, GameScreen.WORLD_TO_SCREEN, 0, color, color,
				color, color);
	}
	
	private Color getColor(float health) {
		if (health < 0.2) {
			return COLOR_HEALTH_LOW;
		}
		else if (health < 0.6) {
			return COLOR_HEALTH_MID;
		}
		else {
			return COLOR_HEALTH_HIGH;
		}
	}
}
