package com.lrchan.qootalk.infrastructure.security.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.lrchan.qootalk.common.error.ErrorCode;
import com.lrchan.qootalk.common.error.GlobalErrorCode;
import com.lrchan.qootalk.common.exception.InfrastructureException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtExceptionFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (InfrastructureException e) {
            setErrorResponse(response, e.getErrorCode());
        } catch (Exception e) {
            setErrorResponse(response, GlobalErrorCode.INTERNAL_ERROR);
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String responseBody = String.format("{\"code\":\"%s\",\"message\":\"%s\"}", errorCode.getCode(), errorCode.getMessage());
        response.getWriter().write(responseBody);
    }
}
