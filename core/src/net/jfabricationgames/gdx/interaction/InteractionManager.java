package net.jfabricationgames.gdx.interaction;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.character.PlayableCharacter;

public class InteractionManager {
	
	private static PlayableCharacter character;
	private static final Comparator<Interactive> distanceComparator = (i1, i2) -> Float.compare(i1.getDistanceFromDwarf(character),
			i2.getDistanceFromDwarf(character));
	
	private static InteractionManager instance;
	
	public static synchronized InteractionManager getInstance() {
		if (instance == null) {
			instance = new InteractionManager();
		}
		return instance;
	}
	
	private Array<Interactive> interactivesInRange;
	
	private InteractionManager() {
		interactivesInRange = new Array<>();
	}
	
	public void interact(PlayableCharacter character) {
		if (!interactivesInRange.isEmpty()) {
			InteractionManager.character = character;
			interactivesInRange.sort(distanceComparator);
			
			Interactive nearest = interactivesInRange.first();
			nearest.interact();
		}
	}
	
	public void movedInRange(Interactive interactive) {
		interactivesInRange.add(interactive);
	}
	
	public void movedOutOfRange(Interactive interactive) {
		interactivesInRange.removeValue(interactive, false);
	}
}
