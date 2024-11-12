package tn.esprit.spring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(infoAPI());
    }

    public Info infoAPI() {
        return new Info().title("Gestion d'un foyer")
                .description("TP étude de cas 2023-2024")
                .contact(contactAPI());
    }

    public Contact contactAPI() {
        Contact contact = new Contact().name("Sirine NAIFAR")
                .email("sirine.naifer@esprit.tn")
                .url("https://www.linkedin.com/in/sirinenaifar/");
        return contact;
    }

    @Bean
    public GroupedOpenApi allPublicApi() {
        return GroupedOpenApi.builder()
                .group("All Management API")
                .pathsToMatch("/**")
                .pathsToExclude("**")
                .build();
    }

    @Bean
    public GroupedOpenApi blocPublicApi() {
        return GroupedOpenApi.builder()
                .group("Only Bloc Management API")
                .pathsToMatch("/bloc/**")
                .pathsToExclude("**")
                .build();
    }

    @Bean
    public GroupedOpenApi chambrePublicApi() {
        return GroupedOpenApi.builder()
                .group("Only Chambre Management API")
                .pathsToMatch("/chambre/**")
                .pathsToExclude("**")
                .build();
    }

    @Bean
    public GroupedOpenApi etudiantPublicApi() {
        return GroupedOpenApi.builder()
                .group("Only Etudiant Management API")
                .pathsToMatch("/etudiant/**")
                .pathsToExclude("**")
                .build();
    }

    @Bean
    public GroupedOpenApi foyerPublicApi() {
        return GroupedOpenApi.builder()
                .group("Only Foyer Management API")
                .pathsToMatch("/foyer/**")
                .pathsToExclude("**")
                .build();
    }

    @Bean
    public GroupedOpenApi reservationPublicApi() {
        return GroupedOpenApi.builder()
                .group("Only Reservation Management API")
                .pathsToMatch("/reservation/**")
                .pathsToExclude("**")
                .build();
    }

    @Bean
    public GroupedOpenApi universitePublicApi() {
        return GroupedOpenApi.builder()
                .group("Only University Management API")
                .pathsToMatch("/universite/**")
                .pathsToExclude("**")
                .build();
    }
}

