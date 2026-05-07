package es.apexexpeditions.library.controller;






// region =========== imports =================
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import java.util.Map;
// endregion






/**
 * custom error controller that overrides default spring boot error handling
 * implements ErrorController to provide specific logic for different HTTP
 * status codes
 */
@Controller
public class CustomErrorController implements ErrorController {
    // region 1. handleError
    /**
     * main entry point for all errors occurring within app
     * Spring Boot automatically redirects requests to "/error" when exception or
     * HTTP error occurs
     *
     * @param request current HTTP request containing error details
     * @return path to mustache template to be rendered
     */
    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer statusCode = status != null ? Integer.valueOf(status.toString()) : 500;
        // En CustomErrorController.java
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (requestUri == null) {
            requestUri = request.getRequestURI(); 
        }

        if (requestUri != null && requestUri.contains("/api/")) { 
            return ResponseEntity
                    .status(statusCode)
                    .body(Map.of(
                            "error", getErrorMessage(statusCode),
                            "status", statusCode,
                            "path", requestUri));
        }

        // web
        if (status != null) {

            if (statusCode == 403) {
                if (requestUri != null) {
                    if (requestUri.startsWith("/admin")) {
                        model.addAttribute("isAdminAttempt", true);
                        model.addAttribute("customMessage",
                                "Lo sentimos, no tienes los permisos suficientes para ver esta sección.\nEsta área está reservada exclusivamente para administradores.");
                    } else if (requestUri.startsWith("/cart")) {
                        model.addAttribute("isUserAttempt", true);
                        model.addAttribute("customMessage",
                                "Para ver su carrito, necesita iniciar sesión con una cuenta.");
                    } else if (requestUri.startsWith("/checkout")) {
                        model.addAttribute("isUserAttempt", true);
                        model.addAttribute("customMessage",
                                "Para finalizar su compra, necesita iniciar sesión con una cuenta.");
                    } else if (requestUri.startsWith("/invoice")) {
                        model.addAttribute("isUserAttempt", true);
                        model.addAttribute("customMessage",
                                "Para ver su factura, necesita iniciar sesión con una cuenta.");
                    }
                }
                return "error/403";
            }

            if (statusCode == 404) {
                return "error/404";
            }
        }

        return "error/404";
    }
    // endregion


    // region 2. getErrorMessage
    private String getErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 404 -> "Resource not found";
            case 403 -> "Access forbidden";
            case 401 -> "Unauthorized";
            case 500 -> "Internal server error";
            default -> "Error occurred";
        };
    }
    // endregion
}
