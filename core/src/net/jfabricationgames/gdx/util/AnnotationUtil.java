package net.jfabricationgames.gdx.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.badlogic.gdx.utils.Array;

public abstract class AnnotationUtil {
	
	public static Array<Method> getMethodsAnnotatedWith(Class<?> type, Class<? extends Annotation> annotation) {
		Array<Method> methods = new Array<Method>();
		for (Method method : type.getMethods()) {
			if (method.isAnnotationPresent(annotation)) {
				methods.add(method);
			}
		}
		return methods;
	}
}
