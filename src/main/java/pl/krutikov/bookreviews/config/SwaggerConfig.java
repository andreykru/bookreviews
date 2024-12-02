package pl.krutikov.bookreviews.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList("basicScheme"))
                .components(
                        new Components().addSecuritySchemes("basicScheme",
                                new SecurityScheme()
                                        .scheme("Basic")
                                        .type(SecurityScheme.Type.HTTP)
                                        .in(SecurityScheme.In.HEADER)
                        )
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("Book reviews API")
                .description("API для сервиса отзывов на книги")
                .version("1.0")
                .contact(apiContact());
    }

    private Contact apiContact() {
        return new Contact()
                .name("@AndreyKrutikov")
                .email("andrusha.krutikov@gmail.com");
    }

}
