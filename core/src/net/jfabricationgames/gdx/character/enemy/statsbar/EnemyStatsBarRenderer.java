package net.jfabricationgames.gdx.character.enemy.statsbar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.jfabricationgames.gdx.constants.Constants;

public abstract class EnemyStatsBarRenderer {
	
	public void drawStatsBar(ShapeRenderer shapeRenderer, float statsValueInPercent, float x, float y, float width) {
		Color color = getColor(statsValueInPercent);
		shapeRenderer.rect(x, y, 0, 0, width * statsValueInPercent, getStatsBarHeight(), Constants.WORLD_TO_SCREEN, Constants.WORLD_TO_SCREEN, 0,
				color, color, color, color);
	}
	
	protected abstract Color getColor(float statsValueInPercent);
	
	protected abstract float getStatsBarHeight();
}
