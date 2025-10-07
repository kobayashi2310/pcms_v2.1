package njb.backend.controller.pcms;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pcms/login")
public class LoginController {

    @GetMapping
    public String loginPage() {
        return "pcms/public/login";
    }

}
