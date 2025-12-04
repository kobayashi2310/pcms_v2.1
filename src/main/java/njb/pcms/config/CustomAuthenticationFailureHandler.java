package njb.pcms.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public CustomAuthenticationFailureHandler() {
        super("/pcms/login?error=true");
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String studentId = request.getParameter("studentId");
        if (studentId == null) {
            studentId = "";
        }
        HttpSession session = request.getSession();

        if (session != null) {
            session.setAttribute("SPRING_SECURITY_LAST_USERNAME", studentId);
        }

        super.onAuthenticationFailure(request, response, exception);
    }

}
