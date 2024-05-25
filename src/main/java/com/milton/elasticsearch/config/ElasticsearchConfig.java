package com.milton.elasticsearch.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.milton.elasticsearch.common.CustomObjectMapper;
import com.milton.elasticsearch.common.ESConfig;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import io.micrometer.common.util.StringUtils;

/**
 * @author MILTON
 */
@Configuration
@ComponentScan(basePackages = { "com.otz", "com.otz.elasticsearch" })
public class ElasticsearchConfig {
	
	@Bean
	public RestClient getRestCleint() {
		
		if(ESConfig.USE_ELASTIC_URL) {
			return createElasticSearchClient(ESConfig.ELASTIC_URL, ESConfig.ELASTIC_USER_NAME, ESConfig.ELASTIC_USER_PASSWORD);
		}
		
		return RestClient.builder(new HttpHost("localhost", 9200)).build();
	}

	@Bean
	public ElasticsearchTransport getElasticsearchTransport() {
		CustomObjectMapper customObjectMapper = new CustomObjectMapper();
		
		return new RestClientTransport(getRestCleint(), new JacksonJsonpMapper(customObjectMapper.getObjectMapper()));
	}

	@Bean
	public ElasticsearchClient getElasticsearchClient() {
		return new ElasticsearchClient(getElasticsearchTransport());
	}
	
	
	private RestClient createElasticSearchClient(String host, String userName, String password) {
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		
		if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
		}
		
		return RestClient.builder(HttpHost.create(host)).setHttpClientConfigCallback(httpClient -> httpClient.setDefaultCredentialsProvider(credentialsProvider)).build();
	}
}
