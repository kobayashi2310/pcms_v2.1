package njb.backend.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String pageTitle = "エラー";
        String errorMessage = "予期せぬエラーが発生しました。";

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                pageTitle = "404 Not Found";
                errorMessage = "お探しのページは見つかりませんでした。";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                pageTitle = "500 Internal Server Error";
                errorMessage = "サーバー内部でエラーが発生しました。";
            }
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }
}
