package net.jfabricationgames.gdx.input.struct;

import java.util.Objects;

import net.jfabricationgames.gdx.input.InputContext;

/**
 * An axis on a specific controller.
 */
public class PlayerAxis {
	
	public int axisCode;
	public int player;
	
	public PlayerAxis(int axisCode, int player) {
		this.axisCode = axisCode;
		this.player = player;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hash(axisCode, player);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerAxis other = (PlayerAxis) obj;
		if (player != other.player && player != InputContext.CONTROLLER_ANY_PLAYER && other.player != InputContext.CONTROLLER_ANY_PLAYER)
			// if one of the players is CONTROLLER_ANY_PLAYER consider them as equal
			return false;
		return axisCode == other.axisCode;
	}
	
	@Override
	public String toString() {
		return "PlayerAxis [axisCode=" + axisCode + ", player=" + player + "]";
	}
}
