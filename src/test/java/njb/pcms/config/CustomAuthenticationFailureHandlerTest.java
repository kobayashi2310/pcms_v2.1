package njb.pcms.config;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomAuthenticationFailureHandlerTest {

    @Test
    void onAuthenticationFailure_ShouldSetSessionAttributeAndRedirect() throws IOException, ServletException {
        CustomAuthenticationFailureHandler handler = new CustomAuthenticationFailureHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException exception = new BadCredentialsException("Bad credentials");

        String studentId = "123456";
        request.setParameter("studentId", studentId);

        handler.onAuthenticationFailure(request, response, exception);

        assertEquals(studentId, request.getSession().getAttribute("SPRING_SECURITY_LAST_USERNAME"));
        assertEquals("/pcms/login?error=true", response.getRedirectedUrl());
    }
}
