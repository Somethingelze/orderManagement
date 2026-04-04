//package com.some.orderservice.filters;
//
//import jakarta.servlet.*;
//import org.slf4j.MDC;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class TraceIdFilter implements Filter {
//
//    private static final String TRACE_ID_KEY = "traceId";
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        try {
//            String traceId = UUID.randomUUID().toString();
//            MDC.put(TRACE_ID_KEY, traceId);
//
//            chain.doFilter(request, response);
//        } finally {
//            MDC.remove(TRACE_ID_KEY);
//        }
//    }
//}