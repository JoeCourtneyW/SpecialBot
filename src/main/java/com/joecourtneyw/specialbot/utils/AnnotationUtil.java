package com.joecourtneyw.specialbot.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotationUtil {

    public static List<Method> getAllMethods(Class<?> klass) {
        return new ArrayList<>(Arrays.asList(klass.getDeclaredMethods()));
    }

    public static boolean hasAnnotation(Method m, Class<? extends Annotation> a) {
        return m.isAnnotationPresent(a);
    }

    public static Annotation getAnnotation(Method m, Class<? extends Annotation> a) {
        return m.getAnnotation(a);
    }

    public static List<Method> getAnnotatedMethods(Class<?> klass, Class<? extends Annotation> a) {
        return getAllMethods(klass).stream().filter(m -> hasAnnotation(m, a)).collect(Collectors.toList());
    }

    public static List<Field> getAllFields(Class<?> klass) {
        return new ArrayList<>(Arrays.asList(klass.getDeclaredFields()));
    }

    public static boolean hasAnnotation(Field f, Class<? extends Annotation> a) {
        return f.isAnnotationPresent(a);
    }

    public static Annotation getAnnotation(Field f, Class<? extends Annotation> a) {
        return f.getAnnotation(a);
    }

    public static List<Field> getAnnotatedFields(Class<?> klass, Class<? extends Annotation> a) {
        return getAllFields(klass).stream().filter(f -> hasAnnotation(f, a)).collect(Collectors.toList());
    }
}
