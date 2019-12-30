package com.example.providerserver.proxy.listener.logging;

import com.example.providerserver.proxy.ParameterSetOperation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface to convert {@link ParameterSetOperation} to {@link String}.
 *
 * Used to convert parameter value to display value.
 *
 * @author complone
 * @see RegisterOutParameterValueConverter
 * @see SetNullParameterValueConverter
 * @since 1.4
 */
public interface ParameterValueConverter {

    // key: int(code) specified in java.sql.Types, value: corresponding field name
    static final Map<Integer, String> SQL_TYPENAME_BY_CODE = new HashMap<Integer, String>() {
        {
            try {
                Class<?> clazz = java.sql.Types.class;
                for (Field field : clazz.getFields()) {
                    String name = field.getName();
                    int code = field.getInt(clazz);
                    this.put(code, name.toUpperCase());
                }
            } catch (Exception e) {
            }
        }
    };


    String getValue(ParameterSetOperation param);

}
