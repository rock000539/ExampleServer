/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.database.model.enums;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.bi.base.database.datasource.DynamicDataSource;
import com.bi.base.database.impl.*;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import com.bi.base.config.WebMvcConfig;
import com.bi.base.database.SqlTemplate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.incrementer.*;

/**
 * Common DatabaseType defining some of the major databases and validationQuery strings.
 * For limiting rows see: @see <a href='https://en.wikipedia.org/wiki/Select_(SQL)#Limiting_result_rows'>site</a>
 * 
 * @author Allen Lin
 * @since 1.0.0
 */
@Slf4j
public enum DatabaseType {

    IMPALA("Impala", "jdbc:impala", "select 1"),
    HIVE("Apache Hive", "jdbc:hive2", "select 1"),
    DERBY("Apache Derby", "jdbc:derby", "select 1"),
    DB2("DB2", "jdbc:db2", "select 1 from sysibm.sysdummy1"),
    FIREBIRD("Firebird", "jdbc:firebird", "select 1 from rdb$database"),
    H2("H2", "jdbc:h2", "select 1"),
    HSQL("HSQL Database Engine", "jdbc:hsqldb", "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS"),
    MYSQL("MySQL", "jdbc:mysql", "select 1"),
    ORACLE("Oracle", "jdbc:oracle", "select 1 from dual"),
    POSTGRES("PostgreSQL", "jdbc:postgresql", "select 1"),
    SQLITE("SQLite", "jdbc:sqlite", "select 1"),
    MSSQL("Microsoft SQL Server", "jdbc:sqlserver", "select 1"),
    SPARKSQL("Spark SQL", "jdbc:hive2", "select 1"),
    SYBASE("Sybase", "jdbc:sybase", "select 1"),
    TERADATA("Teradata", "jdbc:teradata", "select 1");
	
    private static final Map<String, DatabaseType> databaseProductNameMap;

    /**
     * Lookup map based upon the jdbc connection string identifier.
     */
    private static final Map<String, DatabaseType> jdbcConnectionStringMap;

    // Build up the lookup maps.
    static {
        databaseProductNameMap = new HashMap<>();
        jdbcConnectionStringMap = new HashMap<>();
        DatabaseType[] databaseTypes = values();
        for (DatabaseType dbType : databaseTypes) {
            databaseProductNameMap.put(dbType.getProductName(), dbType);
            for (String jdbcConnectionId : dbType.getJdbcConnectionStringIdentifiers()) {
                jdbcConnectionStringMap.put(jdbcConnectionId, dbType);
            }
        }
    }

    /**
     * The database name obtained from the connection.getDatabaseProductName.
     */
    private final String productName;
    
    /**
     * Unique string of jdbc connection to identify the database.
     */
    private final String[] jdbcConnectionStringIdentifiers;
    
    /**
     * The validation Query needed to reconnect.
     */
    private final String validationQuery;

    DatabaseType(String productName, String jdbcUrlIdentifier, String validationQuery) {
        this(productName, new String[]{jdbcUrlIdentifier}, validationQuery);
    }

    DatabaseType(String productName, String[] jdbcConnectionStringIdentifiers, String validationQuery) {
        this.productName = productName;
        this.jdbcConnectionStringIdentifiers = jdbcConnectionStringIdentifiers;
        this.validationQuery = validationQuery;
    }

    /**
     * Return the databaseType from the known database product name.
     *
     * @param productName database product name
     * @return the DatabaseType matching the product name
     */
    public static DatabaseType fromProductName(String productName) throws IllegalArgumentException {
        if (!databaseProductNameMap.containsKey(productName)) {
            throw new IllegalArgumentException("DatabaseType not found for product name: " + productName);
        } else {
            return databaseProductNameMap.get(productName);
        }
    }

    /**
     * Return the databaseType containing the first match to the jdbc connection string.
     *
     * @param connectionString a jdbc url connection string
     * @return the DatabaseType matching the connection String
     */
    public static DatabaseType fromJdbcConnectionString(String connectionString) throws IllegalArgumentException {
        final String lowerCaseConnectionString = connectionString.toLowerCase();

        for (final Map.Entry<String, DatabaseType> entry : jdbcConnectionStringMap.entrySet()) {
            if (lowerCaseConnectionString.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        throw new IllegalArgumentException("DatabaseType not found for jdbc connection String: " + connectionString);
    }

    /**
     * Parse the database product name from the datasource and return the matching database type.
     *
     * @param dataSource datasource
     * @return database type
     * @throws MetaDataAccessException
     */
    public static DatabaseType fromMetaData(DataSource dataSource) throws MetaDataAccessException {
        String databaseProductName = JdbcUtils.extractDatabaseMetaData(dataSource, DatabaseMetaData::getDatabaseProductName);
        databaseProductName = JdbcUtils.commonDatabaseName(databaseProductName);
        try {
            return fromProductName(databaseProductName);
        } catch (IllegalArgumentException e) {
            throw new MetaDataAccessException(e.getMessage());
        }
    }

    /**
     * Parse the database product name from the connection and return the matching database type.
     *
     * @param connection database connection
     * @return database type
     * @throws MetaDataAccessException
     */
    public static DatabaseType fromMetaData(Connection connection) throws MetaDataAccessException {
        String databaseProductName;
        try {
            databaseProductName = connection.getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            throw new MetaDataAccessException(e.getMessage());
        }
        if (databaseProductName != null) {
            databaseProductName = JdbcUtils.commonDatabaseName(databaseProductName);
            try {
                return fromProductName(databaseProductName);
            } catch (IllegalArgumentException e) {
                throw new MetaDataAccessException(e.getMessage());
            }
        } else {
            throw new MetaDataAccessException("Database Type not found for connection");
        }
    }
    
    /**
     * Get {@link com.bi.base.database.SqlTemplate} by current support database.
     *
     * @return SQL format template
     */
    public SqlTemplate getSqlTemplate() {
    	SqlTemplate sqlTemplate;
    	ApplicationContext context = WebMvcConfig.getApplicationContext();
    	
        switch (this.name()) {
			case "IMPALA":
				sqlTemplate = context.getBean(ImpalaSqlTemplate.class);
				break;
			case "HIVE":
				sqlTemplate = context.getBean(HivSqlTemplate.class);
				break;
			case "DERBY":
				sqlTemplate = context.getBean(DerbySqlTemplate.class);
				break;
			case "DB2":
				sqlTemplate = context.getBean(Db2SqlTemplate.class);
				break;
			case "FIREBIRD":
				sqlTemplate = context.getBean(FirebirdSqlTemplate.class);
				break;
			case "H2":
				sqlTemplate = context.getBean(H2SqlTemplate.class);
				break;
			case "HSQL":
				sqlTemplate = context.getBean(HsqlSqlTemplate.class);
				break;
			case "MYSQL":
				sqlTemplate = context.getBean(MySqlSqlTemplate.class);
				break;
			case "ORACLE":
				sqlTemplate = context.getBean(OracleSqlTemplate.class);
				break;
			case "POSTGRES":
				sqlTemplate = context.getBean(PostgreSqlTemplate.class);
				break;
			case "SQLITE":
				sqlTemplate = context.getBean(SqliteSqlTemplate.class);
				break;
			case "MSSQL":
				sqlTemplate = context.getBean(MsSqlSqlTemplate.class);
				break;
			case "SPARKSQL":
				sqlTemplate = context.getBean(SparkSqlSqlTemplate.class);
				break;
			case "SYBASE":
				sqlTemplate = context.getBean(SybaseSqlTemplate.class);
				break;
			case "TERADATA":
				sqlTemplate = context.getBean(TeradataSqlTemplate.class);
				break;
			default:
			    throw new UnsupportedOperationException("Unsupported SQL template with database: " + this.name());
		}
        
		log.debug("Database type: {}", this.productName);
        
		return sqlTemplate;
    }

    /**
     * Get {@link com.bi.base.database.SqlTemplate} by current support database. (support database version decision)
     *
     * @since 1.4.1
     * @return SQL format template
     */
    public static SqlTemplate getSqlTemplate(DataSource dataSource) throws MetaDataAccessException {
        DatabaseType databaseType = fromMetaData(dataSource);
        String databaseProductVersion = JdbcUtils.extractDatabaseMetaData(dataSource, DatabaseMetaData::getDatabaseProductVersion);
    	SqlTemplate sqlTemplate;
    	ApplicationContext context = WebMvcConfig.getApplicationContext();

        switch (databaseType.name()) {
            case "IMPALA":
                sqlTemplate = context.getBean(ImpalaSqlTemplate.class);
                break;
            case "HIVE":
                sqlTemplate = context.getBean(HivSqlTemplate.class);
                break;
            case "DERBY":
                sqlTemplate = context.getBean(DerbySqlTemplate.class);
                break;
            case "DB2":
                sqlTemplate = context.getBean(Db2SqlTemplate.class);
                break;
            case "FIREBIRD":
                sqlTemplate = context.getBean(FirebirdSqlTemplate.class);
                break;
            case "H2":
                sqlTemplate = context.getBean(H2SqlTemplate.class);
                break;
            case "HSQL":
                sqlTemplate = context.getBean(HsqlSqlTemplate.class);
                break;
            case "MYSQL":
                sqlTemplate = context.getBean(MySqlSqlTemplate.class);
                break;
            case "ORACLE":
                sqlTemplate = context.getBean(OracleSqlTemplate.class);
                break;
            case "POSTGRES":
                sqlTemplate = context.getBean(PostgreSqlTemplate.class);
                break;
            case "SQLITE":
                sqlTemplate = context.getBean(SqliteSqlTemplate.class);
                break;
            case "MSSQL":
                int version = Integer.parseInt(databaseProductVersion.substring(0, 2));
                if (version >= 11) {
                    sqlTemplate = context.getBean(MsSql2012SqlTemplate.class);
                } else {
                    sqlTemplate = context.getBean(MsSqlSqlTemplate.class);
                }
                break;
            case "SPARKSQL":
                sqlTemplate = context.getBean(SparkSqlSqlTemplate.class);
                break;
            case "SYBASE":
                sqlTemplate = context.getBean(SybaseSqlTemplate.class);
                break;
            case "TERADATA":
                sqlTemplate = context.getBean(TeradataSqlTemplate.class);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported SQL template with database: " + databaseType.productName);
        }

		log.debug("Database type: {}", databaseType.productName);

		return sqlTemplate;
    }

    /**
     * Get column max value incrementer by current support database.
     *
     * @since 1.2.0
     * @param incrementerName incrementer name
     * @param columnName table column name
     * @return column max value incrementer
     */
    public AbstractColumnMaxValueIncrementer getColumnMaxValueIncrementer(String incrementerName, String columnName) {
        AbstractColumnMaxValueIncrementer columnMaxValueIncrementer;
        ApplicationContext context = WebMvcConfig.getApplicationContext();
        DynamicDataSource dynamicDataSource = context.getBean(DynamicDataSource.class);
        DataSource dataSource = dynamicDataSource.getDataSource();

        switch (this.name()) {
			case "DERBY":
                columnMaxValueIncrementer = new DerbyMaxValueIncrementer(dataSource, incrementerName, columnName);
				break;
			case "HSQL":
                columnMaxValueIncrementer = new HsqlMaxValueIncrementer(dataSource, incrementerName, columnName);
				break;
			case "MYSQL":
                columnMaxValueIncrementer = new MySQLMaxValueIncrementer(dataSource, incrementerName, columnName);
				break;
			case "MSSQL":
                columnMaxValueIncrementer = new SqlServerMaxValueIncrementer(dataSource, incrementerName, columnName);
				break;
            default:
                throw new UnsupportedOperationException("Unsupported ColumnMaxValueIncrementer with database: " + this.name());
		}

		if (log.isDebugEnabled()) log.debug("ColumnMaxValueIncrementer name/column: {}", columnMaxValueIncrementer.getIncrementerName().concat("/").concat(columnMaxValueIncrementer.getColumnName()));

		return columnMaxValueIncrementer;
    }

    /**
     * Get column max value incrementer by current support database.
     *
     * @since 1.2.0
     * @param incrementerName incrementer name
     * @return column max value incrementer
     */
    public AbstractSequenceMaxValueIncrementer getSequenceMaxValueIncrementer(String incrementerName) {
        AbstractSequenceMaxValueIncrementer sequenceMaxValueIncrementer;
        ApplicationContext context = WebMvcConfig.getApplicationContext();
        DynamicDataSource dynamicDataSource = context.getBean(DynamicDataSource.class);
        DataSource dataSource = dynamicDataSource.getDataSource();

        switch (this.name()) {
			case "DB2":
                sequenceMaxValueIncrementer = new Db2LuwMaxValueIncrementer(dataSource, incrementerName);
				break;
			case "H2":
                sequenceMaxValueIncrementer = new H2SequenceMaxValueIncrementer(dataSource, incrementerName);
				break;
			case "HSQL":
                sequenceMaxValueIncrementer = new HsqlSequenceMaxValueIncrementer(dataSource, incrementerName);
				break;
			case "ORACLE":
                sequenceMaxValueIncrementer = new OracleSequenceMaxValueIncrementer(dataSource, incrementerName);
				break;
			case "POSTGRES":
                sequenceMaxValueIncrementer = new PostgresSequenceMaxValueIncrementer(dataSource, incrementerName);
				break;
            default:
                throw new UnsupportedOperationException("Unsupported SequenceMaxValueIncrementer with database: " + this.name());
		}

        if (log.isDebugEnabled()) log.debug("SequenceMaxValueIncrementer: {}", sequenceMaxValueIncrementer.getIncrementerName());

		return sequenceMaxValueIncrementer;
    }

    /**
     * Get the database product name.
     *
     * @return the database product name
     */
    public String getProductName() {
        return this.productName;
    }

    /**
     * Get the array of connection identifiers for this database type.
     *
     * @return the array of unique jdbc connection identifiers
     */
    public String[] getJdbcConnectionStringIdentifiers() {
        return this.jdbcConnectionStringIdentifiers;
    }

    /**
     * Get the validation query for this database.
     *
     * @return the validation query for the database
     */
    public String getValidationQuery() {
        return validationQuery;
    }
    
}
