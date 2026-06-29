package com.mdau.ushirika.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * Wraps every request in a ContentCachingRequestWrapper so the body can be
 * read multiple times. Required for Stripe HMAC-SHA256 webhook verification —
 * the raw body must be available to SecurityConfig and the controller alike.
 * Runs before all other filters (Order = 1).
 */
@Component
@Order(1)
public class RawBodyCachingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            chain.doFilter(new ContentCachingRequestWrapper(httpRequest), response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
