package com.frame.config;


import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SmbConfig {

    @Value("${smbServerUrl}")
    String smbServerUrl;

    @Value("${smbServerUrl}")
    String smbServerUserName;

    @Value("${smbServerUrl}")
    String smbServerPassword;

    @Bean
    public CIFSContext cifsContext() {
            NtlmPasswordAuthenticator auth = new NtlmPasswordAuthenticator(smbServerUrl, smbServerUserName, smbServerPassword);
            CIFSContext baseContext = SingletonContext.getInstance();
            return baseContext.withCredentials(auth);
    }
}
