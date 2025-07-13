package kr.hhplus.be.server.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/*
Swagger API를 사용하기 위한 설정
- 서버의 URL은 localhost:8080으로 설정하였음
 */


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI () {
        return new OpenAPI()
                .info(new Info()
                        .title("이커머스 7팀")
                        .description("이커머스 서비스 MOCK API 명세서"))
                .servers(List.of(new Server()
                        .url("http://localhost:8080").description("Local Server")
                ));
    }

}
