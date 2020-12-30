package net.jfabricationgames.gdx.physics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.jfabricationgames.gdx.map.GameMap;

/**
 * Annotation that can be used to annotate methods of all objects that are present in a {@link GameMap}. All methods that are annotated will be called
 * directly before the world step is executed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface BeforeWorldStep {
	
}
