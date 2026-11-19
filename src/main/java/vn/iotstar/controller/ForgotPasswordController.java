package vn.iotstar.controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.model.ForgotPasswordForm;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.ValidationUtil;

/** Bước 1 quên mật khẩu: nhập email -> gửi OTP. */
@WebServlet("/forgot-password")
public class ForgotPasswordController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/views/forgot-password.jsp").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        ForgotPasswordForm form = new ForgotPasswordForm(req.getParameter("email"));
        String email = form.getEmail();
        Map<String, String> errors = ValidationUtil.validate(form);
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("email", email);
            // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
            resp.setContentType("text/html;charset=UTF-8");
            req.getRequestDispatcher("/views/forgot-password.jsp").include(req, resp);
            return;
        }
        try {
            userService.requestPasswordReset(email);
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("email", email);
            // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
            resp.setContentType("text/html;charset=UTF-8");
            req.getRequestDispatcher("/views/forgot-password.jsp").include(req, resp);
            return;
        }
        HttpSession session = req.getSession(true);
        session.setAttribute(ResetPasswordController.SESSION_EMAIL, email);
        session.setAttribute("resetFlash",
                "Nếu email tồn tại trong hệ thống, mã OTP đã được gửi. Vui lòng kiểm tra hộp thư.");
        resp.sendRedirect(req.getContextPath() + "/reset-password");
    }
}
