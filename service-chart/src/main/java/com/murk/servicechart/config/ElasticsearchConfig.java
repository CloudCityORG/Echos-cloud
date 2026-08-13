package com.murk.servicechart.config;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 自定义 Elasticsearch RestClient，补全 X-Elastic-Product 响应头。
 *
 * Spring Boot 3.2 默认使用 ES 8.x Java 客户端，该客户端会校验响应头中的 X-Elastic-Product。
 * 当连接 ES 7.x 或 OpenSearch 等未返回该头的服务时，启动会报错：
 * "Missing [X-Elastic-Product] header" 或 "Invalid value '...' for 'X-Elastic-Product' header"。
 *
 * 此处提供自定义 RestClientBuilder，通过 HttpClient 响应拦截器移除并重新设置该头，
 * 使 8.x 客户端能够正常与 7.x/OpenSearch 服务通信。
 */
@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris:http://localhost:9200}")
    private List<String> uris;

    @Bean
    public RestClientBuilder elasticsearchRestClientBuilder() {
        String uri = (uris == null || uris.isEmpty()) ? "http://localhost:9200" : uris.get(0);
        RestClientBuilder builder = RestClient.builder(HttpHost.create(uri));
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.addInterceptorLast(new HttpResponseInterceptor() {
                @Override
                public void process(HttpResponse response, HttpContext context) {
                    // 先移除所有已存在的 X-Elastic-Product 头（不区分大小写），再设置正确值
                    response.removeHeaders("X-Elastic-Product");
                    // 注意：ES 8.x 客户端对该头区分大小写，必须为 "Elasticsearch"
                    response.setHeader("X-Elastic-Product", "Elasticsearch");
                }
            });
            return httpClientBuilder;
        });
        return builder;
    }
}
