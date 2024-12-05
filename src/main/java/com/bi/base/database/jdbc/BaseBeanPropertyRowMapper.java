/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.database.jdbc;

import com.bi.base.database.annotation.BaseColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides bean with database row data mapper for <code>@BaseColumn</code>
 * that custom field name with column name mapping.<br>
 * Reference <code>org.springframework.jdbc.core.BeanPropertyRowMapper</code>
 *
 * @author Allen Lin
 * @since 1.3.0
 */
@Slf4j
public class BaseBeanPropertyRowMapper<T> implements RowMapper<T> {

	/** The class we are mapping to */
	@Nullable
	private Class<T> mappedClass;

	/** Whether we're strictly validating */
	private boolean checkFullyPopulated = false;

	/** Whether we're defaulting primitives when mapping a null value */
	private boolean primitivesDefaultedForNullValue = false;

	/** ConversionService for binding JDBC values to bean properties */
	@Nullable
	private ConversionService conversionService = DefaultConversionService.getSharedInstance();

	/** Map of the fields we provide mapping for */
	@Nullable
	private Map<String, PropertyDescriptor> mappedFields;

	/** Set of bean properties we provide mapping for */
	@Nullable
	private Set<String> mappedProperties;

	/**
	 * Create a new {@code BaseBeanPropertyRowMapper} for bean-style configuration.
	 * 
	 * @see #setMappedClass
	 * @see #setCheckFullyPopulated
	 */
	public BaseBeanPropertyRowMapper() {}

	/**
	 * Create a new {@code BaseBeanPropertyRowMapper}, accepting unpopulated
	 * properties in the target bean.
	 * <p>
	 * Consider using the {@link #newInstance} factory method instead,
	 * which allows for specifying the mapped type once only.
	 * 
	 * @param mappedClass the class that each row should be mapped to
	 */
	public BaseBeanPropertyRowMapper(Class<T> mappedClass) {
		initialize(mappedClass);
	}

	/**
	 * Create a new {@code BaseBeanPropertyRowMapper}.
	 * 
	 * @param mappedClass the class that each row should be mapped to
	 * @param checkFullyPopulated whether we're strictly validating that
	 *        all bean properties have been mapped from corresponding database fields
	 */
	public BaseBeanPropertyRowMapper(Class<T> mappedClass, boolean checkFullyPopulated) {
		initialize(mappedClass);
		this.checkFullyPopulated = checkFullyPopulated;
	}

	/**
	 * Set the class that each row should be mapped to.
	 */
	public void setMappedClass(Class<T> mappedClass) {
		if (this.mappedClass == null) {
			initialize(mappedClass);
		} else {
			if (this.mappedClass != mappedClass) {
				throw new InvalidDataAccessApiUsageException("The mapped class can not be reassigned to map to " +
						mappedClass + " since it is already providing mapping for " + this.mappedClass);
			}
		}
	}

	/**
	 * Get the class that we are mapping to.
	 */
	@Nullable
	public final Class<T> getMappedClass() {
		return this.mappedClass;
	}

	/**
	 * Set whether we're strictly validating that all bean properties have been mapped
	 * from corresponding database fields.
	 * <p>
	 * Default is {@code false}, accepting unpopulated properties in the target bean.
	 */
	public void setCheckFullyPopulated(boolean checkFullyPopulated) {
		this.checkFullyPopulated = checkFullyPopulated;
	}

	/**
	 * Return whether we're strictly validating that all bean properties have been
	 * mapped from corresponding database fields.
	 */
	public boolean isCheckFullyPopulated() {
		return this.checkFullyPopulated;
	}

	/**
	 * Set whether we're defaulting Java primitives in the case of mapping a null value
	 * from corresponding database fields.
	 * <p>
	 * Default is {@code false}, throwing an exception when nulls are mapped to Java primitives.
	 */
	public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
		this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
	}

	/**
	 * Return whether we're defaulting Java primitives in the case of mapping a null value
	 * from corresponding database fields.
	 */
	public boolean isPrimitivesDefaultedForNullValue() {
		return this.primitivesDefaultedForNullValue;
	}

	/**
	 * Set a {@link ConversionService} for binding JDBC values to bean properties,
	 * or {@code null} for none.
	 * <p>
	 * Default is a {@link DefaultConversionService}, as of Spring 4.3. This
	 * provides support for {@code java.time} conversion and other special types.
	 * 
	 * @see #initBeanWrapper(BeanWrapper)
	 */
	public void setConversionService(@Nullable ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * Return a {@link ConversionService} for binding JDBC values to bean properties,
	 * or {@code null} if none.
	 */
	@Nullable
	public ConversionService getConversionService() {
		return this.conversionService;
	}

	/**
	 * Initialize the mapping metadata for the given class.
	 *
	 * @author Allen Lin
	 * @since 1.3.0
	 * @param mappedClass the mapped class
	 */
	protected void initialize(Class<T> mappedClass) {
		this.mappedClass = mappedClass;
		this.mappedFields = new HashMap<>();
		this.mappedProperties = new HashSet<>();
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
		Map<String, Field> baseColumnFields = FieldUtils.getFieldsListWithAnnotation(mappedClass, BaseColumn.class).stream()
				.collect(Collectors.toMap(field -> {
					// Mapping rule for PropertyDescriptor's name by paragraph 8.3 of the java beans specification
					String fieldName = field.getName();
					if (fieldName.length() >= 2 && org.apache.commons.lang3.StringUtils.isAllUpperCase(fieldName.substring(1, 2))) {
						return StringUtils.capitalize(fieldName);
					} else {
						return fieldName;
					}
				}, field -> field));
		for (PropertyDescriptor pd : pds) {
			if (pd.getWriteMethod() != null) {
				String fieldName = pd.getName();
				Field baseColumnField = baseColumnFields.get(fieldName);
				if (baseColumnField != null) {
					String column = baseColumnField.getAnnotation(BaseColumn.class).name();
					this.mappedFields.put(column, pd);
				} else {
					this.mappedFields.put(lowerCaseName(fieldName), pd);
					String underscoredName = underscoreName(fieldName);
					if (!lowerCaseName(fieldName).equals(underscoredName)) {
						this.mappedFields.put(underscoredName, pd);
					}
				}
				this.mappedProperties.add(fieldName);
			}
		}
	}

	/**
	 * Convert a name in camelCase to an underscored name in lower case.
	 * Any upper case letters are converted to lower case with a preceding underscore.
	 * 
	 * @param name the original name
	 * @return the converted name
	 * @see #lowerCaseName
	 */
	protected String underscoreName(String name) {
		if (!StringUtils.hasLength(name)) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		result.append(lowerCaseName(name.substring(0, 1)));
		for (int i = 1; i < name.length(); i++) {
			String s = name.substring(i, i + 1);
			String slc = lowerCaseName(s);
			if (!s.equals(slc)) {
				result.append("_").append(slc);
			} else {
				result.append(s);
			}
		}
		return result.toString();
	}

	/**
	 * Convert the given name to lower case.
	 * By default, conversions will happen within the US locale.
	 * 
	 * @param name the original name
	 * @return the converted name
	 */
	protected String lowerCaseName(String name) {
		return name.toLowerCase(Locale.US);
	}

	/**
	 * Extract the values for all columns in the current row.
	 * <p>
	 * Utilizes public setters and result set metadata.
	 *
	 * @author Allen Lin
	 * @since 1.3.0
	 * @see java.sql.ResultSetMetaData
	 */
	@Override
	public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
		Assert.state(this.mappedClass != null, "Mapped class was not specified");
		T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
		initBeanWrapper(bw);

		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Set<String> populatedProperties = (isCheckFullyPopulated() ? new HashSet<>() : null);

		for (int index = 1; index <= columnCount; index++) {
			String column = JdbcUtils.lookupColumnName(rsmd, index);
			String field = lowerCaseName(column.replaceAll(" ", ""));
			PropertyDescriptor pd = null;
			if (this.mappedFields != null) {
				pd = this.mappedFields.get(column);
				// Get from java field naming style.
				if (pd == null) {
					pd = this.mappedFields.get(field);
				}
			}

			if (pd != null) {
				try {
					Object value = getColumnValue(rs, index, pd);
					if (rowNumber == 0 && log.isDebugEnabled()) {
						log.debug("Mapping column '" + column + "' to property '" + pd.getName() +
								"' of type '" + ClassUtils.getQualifiedName(pd.getPropertyType()) + "'");
					}
					try {
						bw.setPropertyValue(pd.getName(), value);
					} catch (TypeMismatchException ex) {
						if (value == null && this.primitivesDefaultedForNullValue) {
							if (log.isDebugEnabled()) {
								log.debug("Intercepted TypeMismatchException for row " + rowNumber +
										" and column '" + column + "' with null value when setting property '" +
										pd.getName() + "' of type '" +
										ClassUtils.getQualifiedName(pd.getPropertyType()) +
										"' on object: " + mappedObject, ex);
							}
						} else {
							throw ex;
						}
					}
					if (populatedProperties != null) {
						populatedProperties.add(pd.getName());
					}
				} catch (NotWritablePropertyException ex) {
					throw new DataRetrievalFailureException("Unable to map column '" + column + "' to property '" + pd.getName() + "'", ex);
				}
			} else {
				// No PropertyDescriptor found
				if (rowNumber == 0 && log.isDebugEnabled()) {
					log.debug("No property found for column '" + column + "' mapped to field '" + field + "'");
				}
			}
		}

		if (populatedProperties != null && !populatedProperties.equals(this.mappedProperties)) {
			throw new InvalidDataAccessApiUsageException("Given ResultSet does not contain all fields " +
					"necessary to populate object of class [" + this.mappedClass.getName() + "]: " +
					this.mappedProperties);
		}

		return mappedObject;
	}

	/**
	 * Initialize the given BeanWrapper to be used for row mapping.
	 * To be called for each row.
	 * <p>
	 * The default implementation applies the configured {@link ConversionService},
	 * if any. Can be overridden in subclasses.
	 * 
	 * @param bw the BeanWrapper to initialize
	 * @see #getConversionService()
	 * @see #BeanWrapper#setConversionService
	 */
	protected void initBeanWrapper(BeanWrapper bw) {
		ConversionService cs = getConversionService();
		if (cs != null) {
			bw.setConversionService(cs);
		}
	}

	/**
	 * Retrieve a JDBC object value for the specified column.
	 * <p>
	 * The default implementation calls
	 * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}.
	 * Subclasses may override this to check specific value types upfront,
	 * or to post-process values return from {@code getResultSetValue}.
	 * 
	 * @param rs is the ResultSet holding the data
	 * @param index is the column index
	 * @param pd the bean property that each result object is expected to match
	 * @return the Object value
	 * @throws SQLException in case of extraction failure
	 * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)
	 */
	@Nullable
	protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
	}

	/**
	 * Static factory method to create a new {@code BaseBeanPropertyRowMapper}
	 * (with the mapped class specified only once).
	 * 
	 * @param mappedClass the class that each row should be mapped to
	 */
	public static <T> BaseBeanPropertyRowMapper<T> newInstance(Class<T> mappedClass) {
		return new BaseBeanPropertyRowMapper<>(mappedClass);
	}
}
