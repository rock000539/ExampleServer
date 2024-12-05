/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */

package com.bi.base.database.annotation.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * Provides to set {@link com.bi.base.database.annotation.BaseAutowired} field
 * and registered association bean.
 *  
 * @author Allen Lin
 * @since 1.3.0
 */
@Component
public class BaseAutowiredAnnotationProcessor implements BeanPostProcessor {

    @Autowired
    private ConfigurableListableBeanFactory configurableBeanFactory;

    @Autowired
    private Environment environment;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> managedBeanClass = bean.getClass();
        ReflectionUtils.FieldCallback fieldCallback = new BaseAutowiredFieldCallback(configurableBeanFactory, environment, bean);
        ReflectionUtils.doWithFields(managedBeanClass, fieldCallback);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
