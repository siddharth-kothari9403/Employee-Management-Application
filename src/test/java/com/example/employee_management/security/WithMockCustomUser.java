package com.example.employee_management.security;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomUserDetailsSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "test_user";
    String password() default "test_password";
    int employeeId() default 1;
    String[] roles() default {"USER"};
}
