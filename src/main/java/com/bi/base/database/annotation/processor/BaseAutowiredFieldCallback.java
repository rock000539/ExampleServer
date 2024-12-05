/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */

package com.bi.base.database.annotation.processor;

import com.bi.base.database.SqlTemplate;
import com.bi.base.database.SqlTemplateProxy;
import com.bi.base.database.annotation.BaseAutowired;
import com.bi.base.database.dao.BaseDao;
import com.bi.base.database.dao.BaseSpDao;
import com.bi.base.database.dao.impl.BaseDaoImpl;
import com.bi.base.database.dao.impl.BaseSpDaoImpl;
import com.bi.base.database.datasource.DynamicDataSource;
import com.bi.base.database.datasource.DynamicDataSourceHolder;
import com.bi.base.database.jdbc.SqlUtilImpl;
import com.bi.base.database.service.BaseService;
import com.bi.base.database.service.BaseSpService;
import com.bi.base.database.service.impl.BaseServiceImpl;
import com.bi.base.database.service.impl.BaseSpServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Provides to set {@link BaseAutowired} field
 * and registered association bean.
 *
 * @author Allen Lin
 * @since 1.3.0
 */
@Slf4j
public class BaseAutowiredFieldCallback implements ReflectionUtils.FieldCallback {

    public final static List<Class> SUPPORTED_CLAZZ = Arrays.asList(BaseDao.class, BaseService.class, BaseSpDao.class, BaseSpService.class);

    private final ConfigurableListableBeanFactory configurableBeanFactory;

    private final Environment environment;

    private final Object bean;

    public BaseAutowiredFieldCallback(ConfigurableListableBeanFactory configurableBeanFactory, Environment environment, Object bean) {
        this.configurableBeanFactory = configurableBeanFactory;
        this.environment = environment;
        this.bean = bean;
    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        Class<?> genericClass = field.getType();
        if (field.isAnnotationPresent(BaseAutowired.class) && SUPPORTED_CLAZZ.contains(genericClass)) {
            Type genType = field.getAnnotatedType().getType();
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            Class<?> entityClazz = (Class<?>) params[0];
            Object beanInstance = getBeanInstance(genericClass, entityClazz, field.getAnnotation(BaseAutowired.class));
            field.setAccessible(true);
            field.set(bean, beanInstance); // Set bean to field
        }
    }

    /**
     * Registered {@link BaseService} {@link BaseDao} {@link BaseSpService} {@link BaseSpDao} bean.
     *
     * @param genericClass {@link BaseService} or {@link BaseDao} or {@link BaseSpService} or {@link BaseSpDao} class
     * @param entityClazz table entity or stored procedure class
     * @param baseAutowired field {@link BaseAutowired}
     * @return {@link BaseService} or {@link BaseDao} or {@link BaseSpService} or {@link BaseSpDao}
     */
    @SuppressWarnings("unchecked")
    private <T> Object getBeanInstance(Class<?> genericClass, Class<T> entityClazz, BaseAutowired baseAutowired) {
        Object beanInstance = null;
        String baseServiceSuffix = "BaseService";
        String baseDaoSuffix = "BaseDao";
        String baseSpServiceSuffix = "BaseSpService";
        String baseSpDaoSuffix = "BaseSpDao";
        String dataSourceName = baseAutowired.dataSourceName();
        String entityName = entityClazz.getSimpleName();
        String beanName;

        if (BaseDao.class.equals(genericClass)) {
            beanName = entityName + baseDaoSuffix;
        } else if (BaseService.class.equals(genericClass)) {
            beanName = entityName + baseServiceSuffix;
        } else if (BaseSpDao.class.equals(genericClass)) {
            beanName = entityName + baseSpDaoSuffix;
        } else if (BaseSpService.class.equals(genericClass)) {
            beanName = entityName + baseSpServiceSuffix;
        } else {
            throw new IllegalArgumentException("Illegal generic class.");
        }

        if (!configurableBeanFactory.containsBean(beanName)) {
            Object registeredBean;
            if (BaseDao.class.equals(genericClass)) {
                registeredBean = createBaseDaoImpl(entityClazz, dataSourceName);

                beanInstance = configurableBeanFactory.initializeBean(registeredBean, beanName);
                configurableBeanFactory.autowireBeanProperties(beanInstance, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
                configurableBeanFactory.registerSingleton(beanName, beanInstance);
            } else if (BaseService.class.equals(genericClass)) {
                String baseDaoBeanName = entityName + baseDaoSuffix;
                BaseDao<T> baseDao;
                if (!configurableBeanFactory.containsBean(baseDaoBeanName)) {
                    baseDao = createBaseDaoImpl(entityClazz, dataSourceName);
                    Object baseDaoBeanInstance = configurableBeanFactory.initializeBean(baseDao, baseDaoBeanName);
                    configurableBeanFactory.autowireBeanProperties(baseDaoBeanInstance, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
                    configurableBeanFactory.registerSingleton(baseDaoBeanName, baseDaoBeanInstance);
                } else {
                    baseDao = (BaseDao<T>) configurableBeanFactory.getBean(baseDaoBeanName);
                }
                registeredBean = createBaseServiceImpl(baseDao);

                beanInstance = configurableBeanFactory.initializeBean(registeredBean, beanName);
                configurableBeanFactory.registerSingleton(beanName, beanInstance);
            } else if (BaseSpDao.class.equals(genericClass)) {
                registeredBean = createBaseSpDaoImpl(entityClazz, dataSourceName);

                beanInstance = configurableBeanFactory.initializeBean(registeredBean, beanName);
                configurableBeanFactory.autowireBeanProperties(beanInstance, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
                configurableBeanFactory.registerSingleton(beanName, beanInstance);
            } else if (BaseSpService.class.equals(genericClass)) {
                String baseSpDaoBeanName = entityName + baseSpDaoSuffix;
                BaseSpDao<T> baseSpDao;
                if (!configurableBeanFactory.containsBean(baseSpDaoBeanName)) {
                    baseSpDao = createBaseSpDaoImpl(entityClazz, dataSourceName);
                    Object baseSpDaoBeanInstance = configurableBeanFactory.initializeBean(baseSpDao, baseSpDaoBeanName);
                    configurableBeanFactory.autowireBeanProperties(baseSpDaoBeanInstance, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
                    configurableBeanFactory.registerSingleton(baseSpDaoBeanName, baseSpDaoBeanInstance);
                } else {
                    baseSpDao = (BaseSpDao<T>) configurableBeanFactory.getBean(baseSpDaoBeanName);
                }
                registeredBean = createBaseSpServiceImpl(baseSpDao);

                beanInstance = configurableBeanFactory.initializeBean(registeredBean, beanName);
                configurableBeanFactory.registerSingleton(beanName, beanInstance);
            }
        } else {
            beanInstance = configurableBeanFactory.getBean(beanName);
        }
        return beanInstance;
    }

    /**
     * Create {@link BaseServiceImpl} by entity class.
     *
     * @param baseDao data access object
     * @param <M> generic parameter for clazz
     * @param <T> generic parameter for clazz
     * @return data access object that service interface
     */
    @SuppressWarnings("rawtypes")
    private <M extends BaseDao<T>, T> BaseServiceImpl createBaseServiceImpl(M baseDao) {
        return new BaseServiceImpl<>(baseDao);
    }

    /**
     * Create {@link BaseSpServiceImpl} by entity class.
     *
     * @since 2.1.0
     * @param baseSpDao access object
     * @param <M> generic parameter for clazz
     * @param <T> generic parameter for clazz
     * @return access object that service interface
     */
    @SuppressWarnings("rawtypes")
    private <M extends BaseSpDao<T>, T> BaseSpServiceImpl createBaseSpServiceImpl(M baseSpDao) {
        return new BaseSpServiceImpl<>(baseSpDao);
    }

    /**
     * Create {@link BaseDaoImpl} by entity class.
     *
     * @param entityClazz table entity class
     * @param dataSourceName datasource name
     * @param <T> generic parameter for clazz
     * @return data access object
     */
    private <T> BaseDaoImpl<T> createBaseDaoImpl(Class<T> entityClazz, String dataSourceName) {
        boolean isDefaultDataSource = StringUtils.isBlank(dataSourceName);
        Assert.isTrue(isDefaultDataSource || DynamicDataSourceHolder.containsDataSourceKey(dataSourceName), "Datasource not found. name: " + dataSourceName);

        String originDataSourceKey = DynamicDataSourceHolder.getDataSourceKey();
        DynamicDataSourceHolder.setDataSourceKey(isDefaultDataSource ? null : dataSourceName); // Set datasource key for fetch specific datasource.
        DynamicDataSource dynamicDataSource = configurableBeanFactory.getBean(DynamicDataSource.class);
        DataSource dataSource = dynamicDataSource.getDataSource();
        JdbcTemplate jdbcTemplate = isDefaultDataSource ? dynamicDataSource.getDefaultJdbcTemplate() : dynamicDataSource.getJdbcTemplate();
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = isDefaultDataSource ? dynamicDataSource.getDefaultNamedParameterJdbcTemplate() : dynamicDataSource.getNamedParameterJdbcTemplate();
        SqlUtilDedicatedSourceImpl sqlUtil = getSqlUtil(dataSource, jdbcTemplate, namedParameterJdbcTemplate);
        BaseDaoImpl<T> baseDaoImpl = new BaseDaoImpl<>(entityClazz, sqlUtil, new SqlTemplateDedicatedSourceProxy(dataSource));
        DynamicDataSourceHolder.setDataSourceKey(originDataSourceKey); // Restore previous datasource key for avoid effect runtime.
        return baseDaoImpl;
    }

    /**
     * Create {@link BaseSpDaoImpl} by entity class.
     *
     * @param entityClazz stored procedure entity class
     * @param dataSourceName datasource name
     * @param <T> generic parameter for clazz
     * @return access object
     */
    private <T> BaseSpDaoImpl<T> createBaseSpDaoImpl(Class<T> entityClazz, String dataSourceName) {
        boolean isDefaultDataSource = StringUtils.isBlank(dataSourceName);
        Assert.isTrue(isDefaultDataSource || DynamicDataSourceHolder.containsDataSourceKey(dataSourceName), "Datasource not found. name: " + dataSourceName);

        String originDataSourceKey = DynamicDataSourceHolder.getDataSourceKey();
        DynamicDataSourceHolder.setDataSourceKey(isDefaultDataSource ? null : dataSourceName); // Set datasource key for fetch specific datasource.
        DynamicDataSource dynamicDataSource = configurableBeanFactory.getBean(DynamicDataSource.class);
        DataSource dataSource = dynamicDataSource.getDataSource();
        JdbcTemplate jdbcTemplate = isDefaultDataSource ? dynamicDataSource.getDefaultJdbcTemplate() : dynamicDataSource.getJdbcTemplate();
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = isDefaultDataSource ? dynamicDataSource.getDefaultNamedParameterJdbcTemplate() : dynamicDataSource.getNamedParameterJdbcTemplate();
        SqlUtilDedicatedSourceImpl sqlUtil = getSqlUtil(dataSource, jdbcTemplate, namedParameterJdbcTemplate);
        BaseSpDaoImpl<T> baseSpDaoImpl = new BaseSpDaoImpl<>(entityClazz, sqlUtil, new SqlTemplateDedicatedSourceProxy(dataSource));
        DynamicDataSourceHolder.setDataSourceKey(originDataSourceKey); // Restore previous datasource key for avoid effect runtime.
        return baseSpDaoImpl;
    }

    /**
     * Get SQL utility
     *
     * @since 2.1.0
     * @param dataSource dedicate datasource
     * @param jdbcTemplate dedicate jdb
     * @param namedParameterJdbcTemplate
     * @return SQL dedicated datasource utility
     */
    private SqlUtilDedicatedSourceImpl getSqlUtil(DataSource dataSource, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        boolean showSql = environment.getProperty("spring.database.showSql", boolean.class, true);
        boolean showSqlArg = environment.getProperty("spring.database.showSqlArg", boolean.class, true);
        SqlUtilDedicatedSourceImpl sqlUtil = new SqlUtilDedicatedSourceImpl(dataSource, jdbcTemplate, namedParameterJdbcTemplate);
        sqlUtil.setShowSql(showSql);
        sqlUtil.setShowSqlArg(showSqlArg);
        return sqlUtil;
    }

    /**
     * Provides dedicated datasource for SQL access utility. base on {@link SqlUtilImpl}
     *
     * @since 1.4.0
     */
    public static class SqlUtilDedicatedSourceImpl extends SqlUtilImpl {

        private final DataSource dataSource;

        private final JdbcTemplate jdbcTemplate;

        private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

        public SqlUtilDedicatedSourceImpl(DataSource dataSource, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
            this.dataSource = dataSource;
            this.jdbcTemplate = jdbcTemplate;
            this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        }

        @Override
        protected void setDefaultSimpleJdbcCall(SimpleJdbcCall simpleJdbcCall) {
            try (Connection connection = dataSource.getConnection()) {
                String schema = connection.getSchema();
                String catalog = connection.getCatalog();
                if (StringUtils.isNotBlank(schema) && StringUtils.isBlank(simpleJdbcCall.getSchemaName())) {
                    simpleJdbcCall.withSchemaName(schema);
                }
                if (StringUtils.isNotBlank(catalog) && StringUtils.isBlank(simpleJdbcCall.getCatalogName())) {
                    simpleJdbcCall.withCatalogName(catalog);
                }
            } catch (SQLException e) {
                log.warn("Set SimpleJdbcCall error: ", e);
            }
        }

        @Override
        protected SqlTemplate getSqlTemplate() {
            return SqlTemplateProxy.getSqlTemplate(dataSource);
        }

        @Override
        protected JdbcTemplate getJdbcTemplate() {
            return jdbcTemplate;
        }

        @Override
        protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
            return namedParameterJdbcTemplate;
        }
    }

    /**
     * Provides dedicated datasource for SQL template. base on {@link SqlTemplateProxy}
     *
     * @since 1.4.0
     */
    public static class SqlTemplateDedicatedSourceProxy extends SqlTemplateProxy {

        private final DataSource dataSource;

        public SqlTemplateDedicatedSourceProxy(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public SqlTemplate getSqlTemplate() {
            return getSqlTemplate(dataSource);
        }
    }
}
