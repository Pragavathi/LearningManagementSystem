package com.example.issuems.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	@Bean
	@LoadBalanced    //Client side load balancing
	public WebClient.Builder builder()
	{
		return WebClient.builder().baseUrl("http://bookms");
	}
	
	@Bean
	@LoadBalanced    //Client side load balancing
	public WebClient webClientBuilder(WebClient.Builder builder)
	{
		return builder.build();
	}

}
