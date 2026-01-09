package njb.pcms.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class IndexController {

    @GetMapping("/")
    public String indexPage() {
        return "index";
    }

    @GetMapping("/.well-known/appspecific/com.chrome.devtools.json")
    @ResponseBody
    public Map<String, Object> devTools() {
        return Map.of();
    }

    @GetMapping("/sotuken")
    public RedirectView sotukenPage() {
        return new RedirectView("http://192.168.0.100");
    }

}
