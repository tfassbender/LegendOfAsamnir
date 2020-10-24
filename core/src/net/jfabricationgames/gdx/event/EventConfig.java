package net.jfabricationgames.gdx.event;

import java.util.Objects;

public class EventConfig {
	
	public EventType eventType;
	public int intValue;
	public float floatValue;
	public boolean booleanValue;
	public String stringValue;
	
	public EventConfig setEventType(EventType eventType) {
		this.eventType = eventType;
		return this;
	}
	
	public EventConfig setIntValue(int intValue) {
		this.intValue = intValue;
		return this;
	}
	
	public EventConfig setFloatValue(float floatValue) {
		this.floatValue = floatValue;
		return this;
	}
	
	public EventConfig setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
		return this;
	}
	
	public EventConfig setStringValue(String stringValue) {
		this.stringValue = stringValue;
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(booleanValue, eventType, floatValue, intValue, stringValue);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventConfig other = (EventConfig) obj;
		return booleanValue == other.booleanValue && eventType == other.eventType
				&& Float.floatToIntBits(floatValue) == Float.floatToIntBits(other.floatValue) && intValue == other.intValue
				&& Objects.equals(stringValue, other.stringValue);
	}
}
