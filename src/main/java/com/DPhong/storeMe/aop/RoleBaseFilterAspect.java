package com.DPhong.storeMe.aop;

import com.DPhong.storeMe.annotation.RoleBaseFilterSpecs;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleBaseFilterAspect {

  private final FilterParser filterParser;
  private final FilterSpecificationConverter filterSpecificationConverter;

  @Around("@annotation(roleBaseFilterSpecs)")
  public Object applyRoleBaseFilter(
      ProceedingJoinPoint joinPoint, RoleBaseFilterSpecs roleBaseFilterSpecs) throws Throwable {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return joinPoint.proceed();
    }

    var authorities = authentication.getAuthorities();
    Specification<?> combinedSpec = null;

    for (var rf : roleBaseFilterSpecs.specs()) {
      boolean match = authorities.stream().anyMatch(auth -> auth.getAuthority().equals(rf.role()));
      if (match) {
        var expr = filterParser.parse(rf.filter());
        Specification<?> spec = filterSpecificationConverter.convert(expr);
        combinedSpec = andSpec(combinedSpec, spec);
      }
    }

    if (combinedSpec != null) {
      Object[] args = joinPoint.getArgs();
      for (int i = 0; i < args.length; i++) {
        if (args[i] instanceof Specification<?> specArg) {
          args[i] = andSpec(specArg, combinedSpec);
          break;
        }
      }
      return joinPoint.proceed(args);
    }

    return joinPoint.proceed();
  }

  @SuppressWarnings("unchecked")
  private Specification<?> andSpec(Specification<?> base, Specification<?> extra) {
    if (base == null) return extra;
    if (extra == null) return base;
    return Specification.where((Specification<Object>) base).and((Specification<Object>) extra);
  }
}
