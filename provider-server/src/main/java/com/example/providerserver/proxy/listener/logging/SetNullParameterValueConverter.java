package com.example.providerserver.proxy.listener.logging;

import com.example.providerserver.proxy.ParameterSetOperation;

/**
 * Convert setNull parameter operation.
 *
 * @author complone
 * @since 1.4
 */
public class SetNullParameterValueConverter implements ParameterValueConverter {

    @Override
    public String getValue(ParameterSetOperation param) {
        Integer sqlType = (Integer) param.getArgs()[1];  // second arg for setNull is always int
        return getDisplayValue(sqlType);
    }

    public String getDisplayValue(Integer sqlType) {

        String sqlTypeName = ParameterValueConverter.SQL_TYPENAME_BY_CODE.get(sqlType);

        StringBuilder sb = new StringBuilder();
        sb.append("NULL");
        if (sqlTypeName != null) {
            sb.append("(" + sqlTypeName + ")");
        }
        return sb.toString();
    }


}
