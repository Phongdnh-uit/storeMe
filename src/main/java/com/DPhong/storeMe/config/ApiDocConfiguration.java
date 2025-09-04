package com.DPhong.storeMe.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocConfiguration {
  private SecurityScheme createAPIKeyScheme() {
    return new SecurityScheme().type(SecurityScheme.Type.HTTP).bearerFormat("JWT").scheme("bearer");
  }

  private Contact createContact() {
    Contact contact =
        new Contact().name("Đặng Nguyễn Huy Phong").email("dangnguyenhuyphong@gmail.com");
    contact.addExtension("Linkedin", "https://www.linkedin.com/in/phong-dang-18420a362/");
    contact.addExtension("Github", "https://github.com/Phongdnh-uit");
    return contact;
  }

  private License createLicense() {
    return new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");
  }

  private Info createInfo() {
    Info infomation = new Info();
    infomation.setTitle("STORE ME API");
    infomation.setVersion("1.0 beta");
    infomation.setContact(createContact());
    infomation.setDescription(
        "Hệ thống lưu trữ file cho phép người dùng upload, download và quản lý file của mình.\n");
    infomation.setLicense(createLicense());
    return infomation;
  }

  @Bean
  public OpenAPI myOpenAPI() {
    return new OpenAPI()
        .info(createInfo())
        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
        .components(
            new Components().addSecuritySchemes("BearerAuthentication", createAPIKeyScheme()));
  }

  @Bean
  public OpenApiCustomizer operationIdCustomizer() {
    return openApi -> {
      openApi
          .getPaths()
          .values()
          .forEach(
              (item) -> {
                item.readOperations()
                    .forEach(
                        operation -> {
                          String operationId = operation.getOperationId();
                          String summary = operation.getSummary();
                          if (operationId != null) {
                            String entityName =
                                operation.getTags().isEmpty()
                                    ? "Default"
                                    : operation.getTags().get(0).replaceAll("\\s+", "");
                            operation.setOperationId(operationId.replace("{Entity}", entityName));
                          }
                          if (summary != null) {
                            String entityName =
                                operation.getTags().isEmpty()
                                    ? "Default"
                                    : operation.getTags().get(0);
                            operation.setSummary(summary.replace("{Entity}", entityName));
                          }
                        });
              });
    };
  }
}
