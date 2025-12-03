package com.multiclinicas.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MultiClínicas API")
                        .version("v1")
                        .description("API para gerenciamento de clínicas médicas multi-tenant")
                        .contact(new Contact()
                                .name("MultiClínicas")
                                .url("https://github.com/alexlsilva7/multi_clinicas_api"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

    @Bean
    public OperationCustomizer globalHeaderCustomizer() {
        return (operation, handlerMethod) -> {
            // Não adiciona o header para endpoints de clínicas (não precisam do tenant)
            String path = handlerMethod.getBeanType().getSimpleName();
            if (path.contains("Clinica")) {
                return operation;
            }
            
            Parameter clinicIdHeader = new Parameter()
                    .in("header")
                    .name("X-Clinic-ID")
                    .description("ID da clínica (tenant) para contexto multi-tenant")
                    .required(true)
                    .example("1")
                    .schema(new io.swagger.v3.oas.models.media.IntegerSchema());
            
            operation.addParametersItem(clinicIdHeader);
            return operation;
        };
    }
}
