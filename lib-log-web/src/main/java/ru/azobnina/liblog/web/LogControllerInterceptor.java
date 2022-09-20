package ru.azobnina.liblog.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class LogControllerInterceptor implements HandlerInterceptor, RequestBodyAdvice, ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        var stringBuilder = new StringBuilder();
        stringBuilder.append("Request: ").append(StringUtils.LF);
        stringBuilder.append(">> Url: ").append(request.getRequestURL()).append(StringUtils.LF);
        stringBuilder.append(">> Method: ").append(request.getMethod()).append(StringUtils.LF);
        stringBuilder.append(">> Headers: ");
        String headers = Collections.list(request.getHeaderNames()).stream()
                .map(headerName -> headerName + "=" + request.getHeader(headerName))
                .collect(Collectors.joining(", "));
        stringBuilder.append("[").append(headers).append("]").append(StringUtils.LF);
        log.info(stringBuilder.toString());

        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        try {
            var message = "Request: " + StringUtils.LF +
                    ">>> Body: " + objectMapper.writeValueAsString(body) + StringUtils.LF;
            log.info(message);
        } catch (JsonProcessingException e) {
            log.error("{} = {}", e.getClass().getName(), e.getLocalizedMessage());
        }

        return body;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {

        var stringBuilder = new StringBuilder();
        stringBuilder.append("Response: ").append(StringUtils.LF);
        stringBuilder.append("<<< Url: ").append(request.getRequestURL()).append(StringUtils.LF);
        stringBuilder.append("<<< Method: ").append(request.getMethod()).append(StringUtils.LF);
        stringBuilder.append("<<< Status: ").append(response.getStatus()).append(StringUtils.LF);
        stringBuilder.append("<<< Headers: ");
        String headers = response.getHeaderNames().stream()
                .map(headerName -> headerName + "=" + response.getHeader(headerName))
                .collect(Collectors.joining(", "));
        stringBuilder.append("[").append(headers).append("]");
        log.info(stringBuilder.toString());
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        try {
            String message = "Response: " + StringUtils.LF +
                    "<< Body: " + objectMapper.writeValueAsString(body) + StringUtils.LF;
            log.info(message);
        } catch (JsonProcessingException e) {
            log.error("{} = {}", e.getClass().getName(), e.getLocalizedMessage());
        }

        return body;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {

        return true;
    }

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {

        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
                                           Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        return inputMessage;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                  Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        return body;
    }
}