package net.jfabricationgames.gdx.animation;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class AnimationConfig implements Cloneable {
	
	public AnimationType type = AnimationType.MULTIPLE_TEXTURES;
	
	public String name;
	public String alias;
	public String atlas;
	public float frameDuration = 0.1f;
	public PlayMode playMode = PlayMode.NORMAL;
	
	public float startScale;
	public float maxScale;
	public float duration;
	
	public String getAlias() {
		return alias != null ? alias : name;
	}
	
	@Override
	public AnimationConfig clone() {
		try {
			return (AnimationConfig) super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
