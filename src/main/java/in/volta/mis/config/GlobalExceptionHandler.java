package in.volta.mis.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request, RedirectAttributes ra) {
        String uri = request.getRequestURI();
        log.warn("Handled MethodNotSupported for method [{}] on URI: {}", ex.getMethod(), uri);

        if (uri.startsWith("/attendance/self-punch")) {
            return "redirect:/attendance/self-punch";
        }
        if (uri.startsWith("/attendance")) {
            return "redirect:/attendance";
        }
        if (uri.startsWith("/mobile/quick-bill")) {
            return "redirect:/mobile/quick-bill";
        }
        if (uri.startsWith("/mobile")) {
            return "redirect:/mobile";
        }
        if (uri.startsWith("/login")) {
            return "redirect:/login";
        }
        return "redirect:/dashboard";
    }
}
