package com.example.demo.config.intoerceptor;

import com.example.demo.config.security.service.AppUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Slf4j
@Component
public class LoggingInterceptor implements AsyncHandlerInterceptor {

    private static final String USER_ID = "USER_ID";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object object) {

        Principal principal = request.getUserPrincipal();

        String userId = null;
        if (principal != null){
            userId = ((AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .getAppUser().getUserId().toString();
        }

        MDC.put(USER_ID, userId);
        log.info("request: {}", request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView){
        MDC.remove(USER_ID);
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception){

    }
}
