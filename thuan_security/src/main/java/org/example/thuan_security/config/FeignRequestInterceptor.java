//package org.example.thuan_security.config;
//
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//
//@Component
//public class FeignRequestInterceptor implements RequestInterceptor {
//
//    @Override
//    public void apply(RequestTemplate template) {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        if (attributes != null) {
//            HttpServletRequest request = attributes.getRequest();
//
//            String token = getTokenFromRequest(request);
//            if (token != null) {
//                template.header("Authorization", "Bearer " + token);
//            }
//        }
//    }
//
//    private String getTokenFromRequest(HttpServletRequest request) {
//        // Lấy token từ header Authorization
//        String authorizationHeader = request.getHeader("Authorization");
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            return authorizationHeader.substring(7);  // Lấy phần sau "Bearer " trong header
//        }
//        return null;
//    }
//}
