package es.apexexpeditions.library.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    
    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        if (httpRequest.getRequestURI().startsWith("/api/")) {
            String ip = getClientIP(httpRequest); 
            
            if (isRateLimited(ip)) {
                httpResponse.setStatus(429); // Too Many Requests
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"error\":\"Too many requests\",\"status\":429}");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isRateLimited(String ip) {
        long currentTime = System.currentTimeMillis();
        RequestCounter counter = requestCounts.computeIfAbsent(ip, k -> new RequestCounter(currentTime));
        
        // Resetear ventana de tiempo de forma segura
        synchronized (counter) {
            if (currentTime - counter.windowStart.get() > 60000) {
                counter.count.set(0);
                counter.windowStart.set(currentTime);
            }
        }
        
        return counter.count.incrementAndGet() > MAX_REQUESTS_PER_MINUTE;
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) return request.getRemoteAddr();
        return xfHeader.split(",")[0]; 
    }

    private static class RequestCounter {
        final AtomicInteger count = new AtomicInteger(0);
        final AtomicLong windowStart = new AtomicLong(0);

        RequestCounter(long startTime) {
            this.windowStart.set(startTime);
        }
    }
}