package net.jfabricationgames.gdx.interaction;

import java.util.Comparator;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import net.jfabricationgames.gdx.animation.AnimationDirector;
import net.jfabricationgames.gdx.animation.AnimationManager;
import net.jfabricationgames.gdx.character.player.PlayableCharacter;

public class InteractionManager {
	
	public static final String ANIMATION_CONFIG_FILE = "config/animation/interaction.json";
	public static final String INTERACTION_ANIMATION = "interrogation";
	
	public static final float INTERACTION_MARK_DEFAULT_OFFSET_FACTOR_X = 0.3f;
	public static final float INTERACTION_MARK_DEFAULT_OFFSET_FACTOR_Y = 0.3f;
	
	private static PlayableCharacter character;
	private static final Comparator<Interactive> distanceComparator = (i1, i2) -> Float.compare(i1.getDistanceToPlayer(character),
			i2.getDistanceToPlayer(character));
	
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
		
		AnimationManager.getInstance().loadAnimations(ANIMATION_CONFIG_FILE);
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
	
	//do not declare static, because otherwise the animation will not be loaded before it's attempted to be used
	public AnimationDirector<TextureRegion> getInteractionAnimationCopy() {
		return AnimationManager.getInstance().getAnimationDirectorCopy(INTERACTION_ANIMATION);
	}
	
	public void resetInteractions() {
		interactivesInRange.clear();
	}
}
