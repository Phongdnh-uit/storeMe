package com.DPhong.storeMe.util;

import com.DPhong.storeMe.annotation.Secured;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class RoleBasedAnnotationIntrospector extends JacksonAnnotationIntrospector {

  @Override
  public boolean hasIgnoreMarker(AnnotatedMember m) {
    Secured secured = m.getAnnotation(Secured.class);
    if (secured != null) {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !authentication.isAuthenticated()) {
        return true; // Ignore if not authenticated
      }
      Set<String> userRoles =
          authentication.getAuthorities().stream()
              .map(grantedAuthority -> grantedAuthority.getAuthority())
              .collect(Collectors.toSet());
      boolean allowed = Arrays.stream(secured.roles()).anyMatch(userRoles::contains);
      return !allowed; // Ignore if user does not have any of the required roles
    }
    return super.hasIgnoreMarker(m);
  }
}
