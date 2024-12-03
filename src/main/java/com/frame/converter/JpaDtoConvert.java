package com.frame.converter;

import com.frame.model.JpaBaseDto;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

public final class JpaDtoConvert implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Map.class, JpaBaseDto.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return map2Pojo(source, targetType.getObjectType());
    }

    public static <T> T map2Pojo(Object entity, Class<T> tClass) {
        T result = BeanUtils.instantiateClass(tClass);
        convertTupleToBean(entity, result);
        return result;
    }

    /**
     * 把Jpa結果中屬性名相同的值複製到實體中
     * @param source jpa結果對象
     * @param target 目標對象實例
     */
    public static void convertTupleToBean(Object source,Object target){
        try {
            convertTupleToBean(source,target,null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 把Jpa結果中屬性名相同的值複製到實體中
     * @param source jpa結果對象
     * @param target 目標對象實例
     * @param ignoreProperties 要忽略的屬性
     */
    public static void convertTupleToBean(Object source, Object target, String... ignoreProperties) throws IllegalAccessException {
        // 目標類
        Class<?> actualEditable = target.getClass();
        // 獲取目標類的屬性信息
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        // 忽略列表
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        // 將 source 轉換為 Map
        Map<String, Object> sourceMap = (Map<String, Object>) source;

        // 遍歷屬性節點信息
        for (PropertyDescriptor targetPd : targetPds) {
            // 獲取 set 方法
            Method writeMethod = targetPd.getWriteMethod();
            // 判斷字段是否可以 set 並且不在忽略列表中
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                String propertyName = targetPd.getName();
                Object value = sourceMap.get(propertyName);

                if (value != null) {
                    try {
                        // 判斷 target 屬性是否 private
                        if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                            writeMethod.setAccessible(true);
                        }

                        // 獲取目標屬性的類型
                        Class<?> propertyType = targetPd.getPropertyType();

                        // 進行類型轉換
                        Object convertedValue = convertValue(value, propertyType);
                        // 寫入 target
                        writeMethod.invoke(target, convertedValue);
                    } catch (Throwable ex) {
                        throw new FatalBeanException(
                                "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                    }
                }
            }
        }
    }

    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }

        if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(value.toString());
        }

        if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(value.toString());
        }

        if (targetType == Boolean.class || targetType == boolean.class) {
            return convertToBoolean(value.toString());
        }

        if (targetType == Double.class || targetType == double.class) {
            return Double.valueOf(value.toString());
        }

        if (targetType == Float.class || targetType == float.class) {
            return Float.valueOf(value.toString());
        }

        if (targetType == String.class) {
            return value.toString();
        }

        // 添加其他類型轉換邏輯

        throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to " + targetType);
    }

    public static Boolean convertToBoolean(Object input) {
        if (input == null) {
            return null;
        }

        if (input instanceof Boolean) {
            return (Boolean) input;
        }

        if (input instanceof Integer) {
            return ((Integer) input) != 0;
        }

        if (input instanceof String) {
            String str = ((String) input).trim().toLowerCase();
            switch (str) {
                case "1":
                case "true":
                    return true;
                case "0":
                case "false":
                    return false;
                default:
                    throw new IllegalArgumentException("Cannot convert to Boolean: " + input);
            }
        }

        String str = input.toString().trim().toLowerCase();
        switch (str) {
            case "1":
            case "true":
                return true;
            case "0":
            case "false":
                return false;
            default:
                throw new IllegalArgumentException("Cannot convert to Boolean: " + input);
        }
    }

}
