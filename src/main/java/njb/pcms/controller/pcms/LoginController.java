package njb.pcms.controller.pcms;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pcms/login")
public class LoginController {

    @GetMapping
    public String loginPage(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/pcms";
        }
        return "pcms/public/login";
    }

}
