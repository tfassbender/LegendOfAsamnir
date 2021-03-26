package net.jfabricationgames.gdx.data.state;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.jfabricationgames.gdx.data.container.MapObjectDataContainer;

/**
 * An annotation to mark methods that shall be executed before adding the state of a stateful object to a {@link MapObjectDataContainer}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeAddStatefulObject {
	
}
