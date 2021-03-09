package com.bb.hotfix;

import java.lang.reflect.Field;

public class ReflectUtils {
    static String TAG = "ReflectUtils";

    public static Object getField(Object instance, String fieldName) {
        Class<?> clazz = instance.getClass();
        try {
            Object o = null;
            while (clazz != null) {
                Field field = null;
                try {
                    field = clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                    continue;
                }
                field.setAccessible(true);
                o = field.get(instance);
                if (o != null) {
                    return o;
                }
            }
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean setField(Object instance, String fieldName, Object value) {
        Class<?> clazz = instance.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
            return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
