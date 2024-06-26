package com.shoalter.mms.data.migration.config.datasource.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Parker Huagn
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "orderSlave.datasource")
public class OrderSlaveDbProperties {
    private String jdbcUrl;
    private String userName;
    private String passWord;
    private int maxLifetime;
    private int borrowConnectionTimeout;
    private int loginTimeout;
    private int maintenanceInterval;
    private int maxIdleTime;
    private String testQuery;
    private String uniqueResourceName;
}
