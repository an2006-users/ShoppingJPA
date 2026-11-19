package vn.iotstar.controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.model.ResetPasswordForm;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.ValidationUtil;

/** Bước 2 quên mật khẩu: nhập OTP + mật khẩu mới. */
@WebServlet("/reset-password")
public class ResetPasswordController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String SESSION_EMAIL = "resetEmail";

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String email = session == null ? null : (String) session.getAttribute(SESSION_EMAIL);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }
        Object flash = session.getAttribute("resetFlash");
        if (flash != null) {
            req.setAttribute("info", flash);
            session.removeAttribute("resetFlash");
        }
        req.setAttribute("maskedEmail", VerifyOtpController.mask(email));
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/views/reset-password.jsp").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        String email = session == null ? null : (String) session.getAttribute(SESSION_EMAIL);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }

        if ("resend".equals(req.getParameter("action"))) {
            try {
                userService.requestPasswordReset(email);
                req.setAttribute("info", "Đã gửi lại mã OTP (nếu email tồn tại). Vui lòng kiểm tra email.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                req.setAttribute("error", e.getMessage());
            }
        } else {
            ResetPasswordForm form = new ResetPasswordForm(req.getParameter("otp"),
                    req.getParameter("password"), req.getParameter("confirm"));
            Map<String, String> errors = ValidationUtil.validate(form);
            if (!errors.isEmpty()) {
                req.setAttribute("errors", errors);
                req.setAttribute("otp", form.getOtp());
            } else {
                try {
                    userService.resetPassword(email, form.getOtp(), form.getPassword());
                    session.removeAttribute(SESSION_EMAIL);
                    resp.sendRedirect(req.getContextPath() + "/login?reset=1");
                    return;
                } catch (IllegalArgumentException e) {
                    req.setAttribute("error", e.getMessage());
                }
            }
        }
        req.setAttribute("maskedEmail", VerifyOtpController.mask(email));
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/views/reset-password.jsp").include(req, resp);
    }
}
