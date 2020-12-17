package com.atguigu.gmall.pms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Configuration
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class MyOssConfig {
    String accessId;
    String accessKey;
    String endpoint;
    String bucket;
}
