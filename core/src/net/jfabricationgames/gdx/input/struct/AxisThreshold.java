package net.jfabricationgames.gdx.input.struct;

/**
 * A threshold value for an axis of a specific player's controller.
 */
public class AxisThreshold extends PlayerAxis {
	
	public float threshold;
	public boolean thresholdPassed = false;//prevent triggering the action every time the value changes, but stays above the threshold
	public String stateName;
	
	public AxisThreshold(int axisCode, int player, float threshold) {
		super(axisCode, player);
		this.threshold = threshold;
	}
	public AxisThreshold(int axisCode, int player, float threshold, String stateName) {
		super(axisCode, player);
		this.threshold = threshold;
		this.stateName = stateName;
	}
	
	public boolean isThresholdPassed(float value) {
		return threshold < 0 && value < threshold || threshold > 0 && value > threshold;
	}
	
	@Override
	public String toString() {
		return "AxisThreshold [threshold=" + threshold + ", thresholdPassed=" + thresholdPassed + ", stateName=" + stateName + "]";
	}
}
