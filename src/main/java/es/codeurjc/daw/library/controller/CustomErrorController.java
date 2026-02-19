package es.codeurjc.daw.library.controller;




import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// used for isadminattempt flag
import org.springframework.ui.Model;




/**
 * custom error controller that overrides default spring boot error handling
 * implements ErrorController to provide specific logic for different HTTP status codes
 */
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * main entry point for all errors occurring within app
     * Spring Boot automatically redirects requests to "/error" when exception or HTTP error occurs
     *
     * @param request current HTTP request containing error details
     * @return path to mustache template to be rendered
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // retrieve HTTP status code (e.g., 404, 403, 500) from request attributes
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            // triggered when user lacks necessary roles (authorization error)
            if (statusCode == 403) {

                // recover original uri that caused error
                String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

                // if uri starts with /admin, set flag to true
                if (requestUri != null && requestUri.startsWith("/admin")) {
                    model.addAttribute("isAdminAttempt", true);
                }

                return "error/403";
            }

            // Triggered when user requests URL that does not map to any Controller
            if (statusCode == 404) {
                return "error/404";
            }
        }

        /**
         * fallback mechanism
         * if error code is not explicitly handled or is null, default to 404 page
         */
        return "error/404";
    }
}