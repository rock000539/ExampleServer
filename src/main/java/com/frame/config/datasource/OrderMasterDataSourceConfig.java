package com.frame.config.datasource;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.frame.config.datasource.properties.OrderMasterDbProperties;
import com.mysql.cj.jdbc.MysqlXADataSource;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * @author Parker Huagn
 * @since 1.0.0
 */
@Configuration
public class OrderMasterDataSourceConfig {

    @Bean("orderMasterDataSource")
    @Primary
    DataSource dataSource(OrderMasterDbProperties orderMasterDbProps) throws SQLException {
        MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        mysqlXADataSource.setUrl(orderMasterDbProps.getJdbcUrl());
        mysqlXADataSource.setPassword(orderMasterDbProps.getPassWord());
        mysqlXADataSource.setUser(orderMasterDbProps.getUserName());
        mysqlXADataSource.setPinGlobalTxToPhysicalConnection(true);

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(mysqlXADataSource);
        xaDataSource.setUniqueResourceName(orderMasterDbProps.getUniqueResourceName());
        xaDataSource.setBorrowConnectionTimeout(orderMasterDbProps.getBorrowConnectionTimeout());
        xaDataSource.setLoginTimeout(orderMasterDbProps.getLoginTimeout());
        xaDataSource.setMaintenanceInterval(orderMasterDbProps.getMaintenanceInterval());
        xaDataSource.setMaxIdleTime(orderMasterDbProps.getMaxIdleTime());
        xaDataSource.setTestQuery(orderMasterDbProps.getTestQuery());
        xaDataSource.setUniqueResourceName(orderMasterDbProps.getUniqueResourceName());
        return xaDataSource;
    }

    @Bean("orderMasterSqlSessionFactory")
    @Primary
    SqlSessionFactory sqlSessionFactory(@Qualifier("orderMasterDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Bean("orderMasterDataSourceTransactionManager")
    @Primary
    DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("orderMasterDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("orderMasterSqlSessionTemplate")
    @Primary
    SqlSessionTemplate sqlSessionTemplate(@Qualifier("orderMasterSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
