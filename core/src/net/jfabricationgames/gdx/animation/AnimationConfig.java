package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class AnimationConfig {
	
	public String name;
	public String alias;
	public String atlas;
	public float frameDuration = 0.1f;
	public PlayMode playMode = PlayMode.NORMAL;
	
	public String getAlias() {
		return alias != null ? alias : name;
	}
}
