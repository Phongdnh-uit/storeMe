package com.DPhong.storeMe.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
    infomation.setTitle("STORE ME API - HỆ THỐNG LƯU TRỮ FILE");
    infomation.setVersion("1.0");
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
            new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
  }
}
