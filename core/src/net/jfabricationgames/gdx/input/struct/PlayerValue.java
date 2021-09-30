package net.jfabricationgames.gdx.input.struct;

/**
 * A value from the controller of a specific player.
 * The value can be a string (e.g. for pov directions) or an integer (e.g. for button codes).
 */
public class PlayerValue {
	
	public static final int CONTROLLER_ANY_PLAYER = -1;
	
	public int player;
	public String value;
	public int intValue;
	
	public PlayerValue(int player, String value) {
		this.player = player;
		this.value = value;
	}
	public PlayerValue(int player, int intValue) {
		this.player = player;
		this.intValue = intValue;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerValue other = (PlayerValue) obj;
		if (player != other.player && player != CONTROLLER_ANY_PLAYER && other.player != CONTROLLER_ANY_PLAYER)
			// if one of the players is CONTROLLER_ANY_PLAYER consider them as equal
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		if (intValue != other.intValue)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "PlayerValue [player=" + player + ", value=" + value + ", intValue=" + intValue + "]";
	}
}
