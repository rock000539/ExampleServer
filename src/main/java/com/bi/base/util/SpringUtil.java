/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Access already declared bean.
 * 
 * @author Allen Lin
 * @since 1.0.0
 */
@Component
public final class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext context;

	/**
	 * Get bean by bean name.
	 * 
	 * @param beanName bean name
	 * @return bean
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName) {
		return (T) context.getBean(beanName);
	}

	/**
	 * Get bean by bean class.
	 * 
	 * @param clazz bean class
	 * @return bean
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<?> clazz) {
		return (T) context.getBean(clazz);
	}

	/**
	 * Get bean exactly.
	 * 
	 * @param beanName bean name
	 * @param clazz bean class
	 * @return bean
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName, Class<?> clazz) {
		return (T) context.getBean(beanName, clazz);
	}

	/**
	 * Create bean by class.
	 *
	 * @param clazz bean class
	 * @return bean
	 */
	public static <T> T createBean(Class<?> clazz) throws ClassNotFoundException {
		return createBean(clazz.getName());
	}

	/**
	 * Create bean by class name.
	 * 
	 * @param clazzName bean class name
	 * @return bean
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createBean(String clazzName) throws ClassNotFoundException {
		String beanName = Class.forName(clazzName).getName();
		ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) context;
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
		if (!beanFactory.containsBean(beanName)) {
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(clazzName);
			beanDefinitionBuilder.setScope("singleton"); // Prototype
			beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
		}
		return (T) beanFactory.getBean(beanName);
	}

	public static ApplicationContext getContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
}
