package vn.iotstar.controller.profile;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.model.OtpForm;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.ValidationUtil;

/**
 * Xác nhận email mới bằng OTP: /profile/verify-email
 * POST action = verify (mặc định) | resend | cancel
 */
@WebServlet(urlPatterns = { "/profile/verify-email" })
public class VerifyEmailController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String VIEW = "/views/web/verify-email.jsp";

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = userService.findById((Integer) session.getAttribute("userId"));
        if (user == null || user.getPendingEmail() == null) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }
        ProfileController.moveFlash(session, "emailFlash", req, "info");
        ProfileController.moveFlash(session, "emailFlashError", req, "error");
        req.setAttribute("user", user);
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher(VIEW).include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute("userId");
        String action = req.getParameter("action");

        if ("cancel".equals(action)) {
            userService.cancelEmailChange(userId);
            session.setAttribute("profileFlash", "Đã huỷ yêu cầu đổi email.");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if ("resend".equals(action)) {
            try {
                userService.resendEmailChangeOtp(userId);
                req.setAttribute("info", "Đã gửi lại mã OTP đến email mới.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                req.setAttribute("error", e.getMessage());
            }
        } else {
            OtpForm form = new OtpForm(req.getParameter("otp"));
            Map<String, String> errors = ValidationUtil.validate(form);
            if (!errors.isEmpty()) {
                req.setAttribute("errors", errors);
            } else {
                try {
                    String email = userService.confirmEmailChange(userId, form.getOtp());
                    session.setAttribute("profileFlash", "Xác nhận thành công! Email của bạn đã đổi thành " + email + ".");
                    resp.sendRedirect(req.getContextPath() + "/profile");
                    return;
                } catch (IllegalArgumentException e) {
                    req.setAttribute("error", e.getMessage());
                }
            }
        }

        User user = userService.findById(userId);
        if (user == null || user.getPendingEmail() == null) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }
        req.setAttribute("user", user);
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher(VIEW).include(req, resp);
    }
}
