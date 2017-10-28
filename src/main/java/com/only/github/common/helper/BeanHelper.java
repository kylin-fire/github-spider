package com.only.github.common.helper;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * 扩展Apache的BeanUtil
 * User: only
 * Date: 2014/8/22
 * Time: 21:37
 */
public abstract class BeanHelper {
    /**
     * 拷贝相同名称属性的值, 不同类型的属性会尝试转换
     *
     * @param desc 目标对象
     * @param orig 原始对象
     * @param <T>  对象类型
     * @return 目标对象
     */
    public static <T> T copyProperties(T desc, Object orig) {
        try {
            BeanUtils.copyProperties(orig, desc);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        return desc;
    }

    /**
     * 拷贝相同名称属性, 属性的类型必须相同，性能不是很好，慎用
     *
     * @param desc 目标对象
     * @param orig 原始对象
     * @param <T>  对象类型
     * @return 目标对象
     */
    public static <T> T copyDescriptors(T desc, Object orig) {
        try {
            PropertyUtils.copyProperties(desc, orig);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        return desc;
    }

    public static Map<String, Object> toMap(Object object) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            Map<String, Object> result = Maps.newHashMap();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性，且属性无写方法
                if (!key.equals("class") && property.getWriteMethod() != null && property.getReadMethod() != null) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(object);

                    if (value != null) {
                        result.put(key, value);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public static String formatDate(Date date, String format) {
        if (null == date || StringUtils.isBlank(format)) {
            return null;
        }

        try {
            SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.FULL, Locale.ENGLISH);

            formatter.applyPattern(format);

            return formatter.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date toDate(String date, String format) {
        if (StringUtils.isBlank(date) || StringUtils.isBlank(format)) {
            return null;
        }

        try {
            SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.FULL, Locale.ENGLISH);

            formatter.applyPattern(format);

            return formatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
