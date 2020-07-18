package net.jfabricationgames.gdx.character.animation;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class AnimationConfig {
	
	private String name;
	private String atlas;
	private float frameDuration = 0.1f;
	private PlayMode playMode = PlayMode.LOOP;
	
	public AnimationConfig() {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAtlas() {
		return atlas;
	}
	public void setAtlas(String atlas) {
		this.atlas = atlas;
	}
	
	public float getFrameDuration() {
		return frameDuration;
	}
	public void setFrameDuration(float frameDuration) {
		this.frameDuration = frameDuration;
	}
	
	public PlayMode getPlayMode() {
		return playMode;
	}
	public void setPlayMode(PlayMode playMode) {
		this.playMode = playMode;
	}
	
	@Override
	public String toString() {
		return "AnimationConfig [name=" + name + ", atlas=" + atlas + ", frameDuration=" + frameDuration + ", playMode=" + playMode + "]";
	}
}
