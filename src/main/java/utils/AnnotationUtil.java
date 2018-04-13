package utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnotationUtil {

	public static List<Method> getAllMethods(Class<?> klass) {
		return new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
	}

	public static boolean hasAnnotation(Method m, Class<? extends Annotation> a) {
		return m.isAnnotationPresent(a);
	}

	public static Annotation getAnnotation(Method m, Class<? extends Annotation> a) {
		return m.getAnnotation(a);
	}
	public static List<Method> getAnnotatedMethods(Class<?> klass, Class<? extends Annotation> a){
		List<Method> annotated = new ArrayList<Method>();
		for(Method m : getAllMethods(klass)){
			if(hasAnnotation(m, a)){
				annotated.add(m);
			}
		}
		return annotated;
	}
}
