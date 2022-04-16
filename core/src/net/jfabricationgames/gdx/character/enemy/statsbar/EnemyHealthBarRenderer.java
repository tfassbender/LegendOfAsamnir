package net.jfabricationgames.gdx.character.enemy.statsbar;

import com.badlogic.gdx.graphics.Color;

public class EnemyHealthBarRenderer extends EnemyStatsBarRenderer {
	
	private static final float HEALTH_BAR_HEIGHT = 2f;
	private static final Color COLOR_HEALTH_HIGH = Color.GREEN;
	private static final Color COLOR_HEALTH_MID = Color.YELLOW;
	private static final Color COLOR_HEALTH_LOW = Color.RED;
	
	@Override
	protected Color getColor(float health) {
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
	
	@Override
	protected float getStatsBarHeight() {
		return HEALTH_BAR_HEIGHT;
	}
}
