package net.jfabricationgames.gdx.character.enemy.statsbar;

import com.badlogic.gdx.graphics.Color;

public class EnemyEnduranceBarRenderer extends EnemyStatsBarRenderer {
	
	private static final float ENDURANCE_BAR_HEIGHT = 1f;
	private static final Color COLOR_ENDURANCE = Color.YELLOW;
	
	@Override
	protected Color getColor(float endurance) {
		return COLOR_ENDURANCE;
	}
	
	@Override
	protected float getStatsBarHeight() {
		return ENDURANCE_BAR_HEIGHT;
	}
}