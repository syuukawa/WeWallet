package net.wzero.wewallet.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringCloudApplication
public class WewalletGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(WewalletGatewayApplication.class, args);
	}
}

