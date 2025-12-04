package njb.pcms.controller.pcms;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import njb.pcms.constant.ViewNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pcms/login")
@Slf4j
public class LoginController {

    /**
     * GET /pcms/login
     * ログインページを表示します。
     *
     * @param request HTTPリクエスト
     * @return ログインページのビュー名
     */
    @GetMapping
    public String loginPage(HttpServletRequest request) {
        log.debug("Accessing login page");
        if (request.getUserPrincipal() != null) {
            log.debug("User already authenticated, redirecting to home");
            return "redirect:/pcms";
        }
        return ViewNames.PCMS_LOGIN;
    }

}
