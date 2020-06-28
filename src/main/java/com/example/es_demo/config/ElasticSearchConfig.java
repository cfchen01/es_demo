package com.example.es_demo.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ES Config
 * @author zdl
 */
@Configuration
@ConditionalOnProperty(prefix = "elasticsearch", name = "open", havingValue = "true", matchIfMissing = true)
public class ElasticSearchConfig {

    @Value("${elasticsearch.hostName}")
    private String hostName;
    @Value("${elasticsearch.rest-port}")
    private Integer restPort;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(hostName, restPort, "http"))
                        .setRequestConfigCallback(builder -> builder.setConnectTimeout(600000).setSocketTimeout(600000))
        );
        return client;
    }


}
