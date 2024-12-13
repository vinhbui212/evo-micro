//package org.example.thuan_security.config;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.example.thuan_security.request.HttpRequestWrapper;
//import org.example.thuan_security.response.HttpResponseWrapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@WebFilter("/*")
//@Component// Áp dụng filter cho tất cả các request
//public class RequestLoggingFilter implements Filter {
//
//    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // Khởi tạo nếu cần
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        // Wrap request và response để lấy body
//        HttpServletRequest wrappedRequest = new HttpRequestWrapper((HttpServletRequest) request);
//        HttpServletResponse wrappedResponse = new HttpResponseWrapper((HttpServletResponse) response);
//
//        // Log request information
//        String method = wrappedRequest.getMethod();
//        String uri = wrappedRequest.getRequestURI();
//        String queryString = wrappedRequest.getQueryString();
//        String fullUrl = uri + (queryString != null ? "?" + queryString : "");
////        logger.info("Received request: Method={}, URL={}", method, fullUrl);
////        logger.info("Request Body: {}", ((HttpRequestWrapper) wrappedRequest).getBody());
//
//        // Tiến hành chain để tiếp tục xử lý request
//        chain.doFilter(wrappedRequest, wrappedResponse);
//
//        // Log response status và body
//        int status = wrappedResponse.getStatus();
//        String responseBody = ((HttpResponseWrapper) wrappedResponse).getResponseBody();
//
//        // Log response information
////        logger.info("Response Status: {}", status);
////        logger.info("Response Body: {}", responseBody);
//
//        // Đảm bảo flush buffer để trả về dữ liệu cho client
//        wrappedResponse.flushBuffer();
//    }
//
//    @Override
//    public void destroy() {
//        // Dọn dẹp tài nguyên nếu cần
//    }
//}
