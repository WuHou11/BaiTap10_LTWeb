package vn.iotstar.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);

        String role = null;
        if (session != null && session.getAttribute("role") != null) {
            role = session.getAttribute("role").toString();
        }

        // Nếu vào trang admin
        if (uri.startsWith("/admin")) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                response.sendRedirect("/login");
                return false;
            }
        }

        // Nếu vào trang user
        if (uri.startsWith("/user")) {
            if (!"USER".equalsIgnoreCase(role)) {
                response.sendRedirect("/login");
                return false;
            }
        }

        return true;
    }
}