package net.jfabricationgames.gdx.data.state;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.jfabricationgames.gdx.data.GameDataService;
import net.jfabricationgames.gdx.data.container.GameDataContainer;

/**
 * Annotation for methods that shall be executed before the state of a {@link GameDataContainer} is persisted. 
 * The objects that are checked for these methods need to be added in {@link GameDataService}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforePersistState {
	
}
