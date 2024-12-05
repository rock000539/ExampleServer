/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.service.impl;

import com.bi.base.config.WebMvcConfig;
import com.bi.base.database.annotation.BaseSequence;
import com.bi.base.database.datasource.DynamicDataSource;
import com.bi.base.database.model.enums.DatabaseType;
import com.bi.base.database.service.Generator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.support.MetaDataAccessException;

import javax.naming.ConfigurationException;
import javax.sql.DataSource;
import java.lang.reflect.Field;

/**
 * This implementation provides sequence value.
 * 
 * @author Allen Lin
 * @since 1.2.0
 */
public class SequenceGenerator implements Generator {

	@Override
	public Object getValue(Object entity, Field field) {
		ApplicationContext context = WebMvcConfig.getApplicationContext();
        DynamicDataSource dynamicDataSource = context.getBean(DynamicDataSource.class);
		DataSource dataSource = dynamicDataSource.getDataSource();
        try {
            BaseSequence baseSequence = field.getAnnotation(BaseSequence.class);
            if (baseSequence != null) {
                Class<?> type = field.getType();
                String name = baseSequence.name();
                String catalog = baseSequence.catalog();
                String schema = baseSequence.schema();
                if (StringUtils.isNotBlank(schema)) name = schema.concat(".").concat(name);
                if (StringUtils.isNotBlank(catalog)) name = catalog.concat(".").concat(name);
                if (int.class.equals(type)) {
                    return DatabaseType.fromMetaData(dataSource).getSequenceMaxValueIncrementer(name).nextIntValue();
                } else if (Long.class.equals(type)) {
                    return DatabaseType.fromMetaData(dataSource).getSequenceMaxValueIncrementer(name).nextLongValue();
                } else if (String.class.equals(type)) {
                    return DatabaseType.fromMetaData(dataSource).getSequenceMaxValueIncrementer(name).nextStringValue();
                } else {
                    throw new ConfigurationException("Did not support field type: " + type); 
                }
            } else {
                throw new ConfigurationException("Did not set BaseSequence annotation");
            }
        } catch (MetaDataAccessException | ConfigurationException e) {
            throw new RuntimeException(e);
        }
	}
}
