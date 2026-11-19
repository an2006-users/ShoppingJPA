package vn.iotstar.controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.model.LoginForm;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.ValidationUtil;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        if ("1".equals(req.getParameter("activated"))) {
            req.setAttribute("success", "Kích hoạt tài khoản thành công! Hãy đăng nhập.");
        } else if ("1".equals(req.getParameter("reset"))) {
            req.setAttribute("success", "Đặt lại mật khẩu thành công! Hãy đăng nhập bằng mật khẩu mới.");
        }
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/views/login.jsp").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        LoginForm form = new LoginForm(req.getParameter("username"), req.getParameter("password"));
        String username = form.getUsername();

        // Validation phía server (Bean Validation)
        Map<String, String> errors = ValidationUtil.validate(form);
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("username", username);
            // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
            resp.setContentType("text/html;charset=UTF-8");
            req.getRequestDispatcher("/views/login.jsp").include(req, resp);
            return;
        }

        User user = userService.login(username, form.getPassword());
        if (user != null && !user.isActive()) {
            // Đúng mật khẩu nhưng chưa kích hoạt -> gửi OTP mới và chuyển sang trang xác nhận
            HttpSession session = req.getSession(true);
            session.setAttribute(VerifyOtpController.SESSION_EMAIL, user.getEmail());
            try {
                userService.sendActivationOtp(user.getEmail());
                session.setAttribute("otpFlash", "Tài khoản chưa được kích hoạt. Mã OTP đã được gửi đến email của bạn.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                // ví dụ: vừa gửi mã cách đây chưa đến 30 giây -> dùng mã hiện có
                session.setAttribute("otpFlashError", e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + "/verify-otp");
            return;
        }
        if (user != null) {
            // Tạo session mới để tránh session fixation
            HttpSession old = req.getSession(false);
            if (old != null) {
                old.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("fullname", user.getFullname());
            session.setAttribute("avatar", user.getImages()); // URL ảnh Cloudinary (có thể null)
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
        req.setAttribute("username", username);
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/views/login.jsp").include(req, resp);
    }
}
