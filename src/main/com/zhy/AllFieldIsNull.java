package com.zhy;

import java.lang.reflect.Field;

/**
 * @author zhy
 * 判断类的属性值是否都为null
 * 如果为null就返回true
 */
public class AllFieldIsNull {
    public static boolean allFieldIsNull(Object obj) throws IllegalAccessException {
        for (Field field : obj.getClass().getDeclaredFields()) {
            // 把私有属性公有化
            field.setAccessible(true);
            Object object = field.get(obj);
            if (object != null) {
                return false;
            }
        }
        return true;
    }
}
