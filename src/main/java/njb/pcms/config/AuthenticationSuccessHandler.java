package njb.pcms.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import njb.pcms.constant.UrlPaths;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * Spring Security コンテキストで認証成功後の動作を処理します。
 * このクラスは {@link SavedRequestAwareAuthenticationSuccessHandler} を拡張し、
 * ログイン成功後にユーザーのロールに基づいてリダイレクトするためのカスタムロジックを定義します。
 * 主な機能:<br/>
 * - 「ROLE_ADMIN」権限を持つユーザーを管理ダッシュボードにリダイレクトします。<br/>
 * - その他のすべてのユーザーを、設定で指定されたデフォルトのターゲットURLにリダイレクトします。<br/>
 *
 * 意図された動作は、認証後にユーザーが役割に応じてアプリケーション
 * のさまざまなセクションに適切にリダイレクトされるようにすることです。
 * このクラスには {@code @Component} アノテーションが付けられており、
 * Spring フレームワークによって自動的に検出および管理されます。
 */
@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public AuthenticationSuccessHandler() {
        setDefaultTargetUrl(UrlPaths.PCMS_HOME);
    }

    /**
     * Spring Security コンテキストで認証が成功した際に実行するアクションを処理します。
     * 割り当てられたロールに基づいて、ユーザーを特定の URL にリダイレクトします。
     *
     * @param request リクエストに関連付けられた HttpServletRequest オブジェクト
     * @param response the HttpServletResponse object to send the HTTP response
     * @param authentication the Authentication object containing the details of the authenticated user
     * @throws ServletException if an error occurs during request handling
     * @throws IOException if an input or output error occurs during request processing
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect(UrlPaths.PCMS_ADMIN);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

}
