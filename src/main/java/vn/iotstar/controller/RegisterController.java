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
import vn.iotstar.model.RegisterForm;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.FieldValidationException;
import vn.iotstar.util.ValidationUtil;

@WebServlet("/register")
public class RegisterController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/views/register.jsp").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        RegisterForm form = new RegisterForm(req.getParameter("fullname"), req.getParameter("email"),
                req.getParameter("username"), req.getParameter("password"), req.getParameter("confirm"));

        Map<String, String> errors = ValidationUtil.validate(form);
        User user = null;
        if (errors.isEmpty()) {
            try {
                user = userService.register(form.getUsername(), form.getPassword(), form.getFullname(), form.getEmail());
            } catch (FieldValidationException e) {
                errors.put(e.getField(), e.getMessage()); // vd: username / email đã tồn tại
            } catch (IllegalArgumentException e) {
                req.setAttribute("error", e.getMessage());
            }
        }
        if (user == null) {
            req.setAttribute("errors", errors);
            req.setAttribute("form", form); // giữ lại dữ liệu đã nhập (JSP không hiện lại mật khẩu)
            // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
            resp.setContentType("text/html;charset=UTF-8");
            req.getRequestDispatcher("/views/register.jsp").include(req, resp);
            return;
        }

        // Tài khoản đã tạo (chưa kích hoạt) -> gửi OTP rồi chuyển sang trang nhập mã
        HttpSession session = req.getSession(true);
        session.setAttribute(VerifyOtpController.SESSION_EMAIL, user.getEmail());
        try {
            userService.sendActivationOtp(user.getEmail());
            session.setAttribute("otpFlash", "Chúng tôi đã gửi mã OTP đến email của bạn.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Gửi mail lỗi: vẫn cho sang trang xác nhận để bấm "Gửi lại mã"
            session.setAttribute("otpFlashError", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/verify-otp");
    }
}
