package net.jfabricationgames.gdx.character.animation;

import java.util.List;

public class AnimationConfigList {
	
	private List<AnimationConfig> configList;
	
	public AnimationConfigList() {
		
	}
	
	public List<AnimationConfig> getConfigList() {
		return configList;
	}
	
	public void setConfigList(List<AnimationConfig> configList) {
		this.configList = configList;
	}
	
	@Override
	public String toString() {
		return "AnimationConfigList [configList=" + configList + "]";
	}
}
