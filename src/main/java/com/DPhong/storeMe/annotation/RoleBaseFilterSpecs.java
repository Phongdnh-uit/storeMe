package com.DPhong.storeMe.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RoleBaseFilterSpecs {
  FilterSpec[] specs();

  @Retention(RetentionPolicy.RUNTIME)
  @Target({})
  public @interface FilterSpec {
    String role();

    String description() default "";

    String filter();
  }
}
