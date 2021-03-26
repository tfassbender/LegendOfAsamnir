package net.jfabricationgames.gdx.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class AnnotationUtil {
	
	private AnnotationUtil() {}
	
	public static Array<Method> getMethodsAnnotatedWith(Class<?> type, Class<? extends Annotation> annotation) {
		Array<Method> methods = new Array<Method>();
		for (Method method : type.getMethods()) {
			if (method.isAnnotationPresent(annotation)) {
				methods.add(method);
			}
		}
		return methods;
	}
	
	public static void executeAnnotatedMethods(Class<? extends Annotation> annotation, Object object) {
		Class<?> objectType = object.getClass();
		Array<Method> annotatedMethods = AnnotationUtil.getMethodsAnnotatedWith(objectType, annotation);
		for (Method method : annotatedMethods) {
			try {
				method.invoke(object, new Object[0]);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				Gdx.app.error(AnnotationUtil.class.getSimpleName(), "could not invoke method '" + method.getName() + "' annotated '"
						+ annotation.getSimpleName() + "' on object of type '" + objectType.getSimpleName() + "'", e);
			}
		}
	}
}
