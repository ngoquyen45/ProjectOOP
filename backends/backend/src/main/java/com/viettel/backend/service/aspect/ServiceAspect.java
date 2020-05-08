package com.viettel.backend.service.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.viettel.backend.oauth2.core.UserLogin;

@Aspect
@Component
public class ServiceAspect {

    @Pointcut(value = "execution(public * *(..))")
    public void anyPublicMethod() {
    }

    @Before(value = "anyPublicMethod()" + " && (@within(com.viettel.backend.service.aspect.RolePermission) "
            + "|| @annotation(com.viettel.backend.service.aspect.RolePermission))")
    public void checkRolePermission(JoinPoint joinPoint) throws Throwable {
        String[] roleAlloweds = null;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RolePermission rolePermission = AnnotationUtils.findAnnotation(method, RolePermission.class);
        if (rolePermission == null) {
            rolePermission = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RolePermission.class);
        }

        if (rolePermission != null) {
            roleAlloweds = rolePermission.value();
        } else {
            throw new UnsupportedOperationException("role permission not found");
        }

        Object[] args = joinPoint.getArgs();
        if (args.length != 0 && args[0] != null && args[0] instanceof UserLogin) {
            UserLogin userLogin = (UserLogin) args[0];

            if (roleAlloweds != null) {
                for (String roleAllowed : roleAlloweds) {
                    if (userLogin.isRole(roleAllowed)) {
                        return;
                    }
                }
            }

            throw new UnsupportedOperationException(String.format("Role '%s' not allow for this method",
                    userLogin.getRole()));
        }
    }

}
