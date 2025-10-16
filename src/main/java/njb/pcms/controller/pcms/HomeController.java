package njb.pcms.controller.pcms;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pcms")
public class HomeController {

    @GetMapping
    public String homePage() {
        return "pcms/home";
    }

}
