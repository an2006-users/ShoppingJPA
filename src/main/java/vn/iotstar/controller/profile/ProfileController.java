package vn.iotstar.controller.profile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.User;
import vn.iotstar.model.ProfileForm;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.CloudinaryUtil;
import vn.iotstar.util.FieldValidationException;
import vn.iotstar.util.FileValidator;
import vn.iotstar.util.ValidationUtil;

/**
 * Hồ sơ người dùng: /profile
 * - Cập nhật họ tên, số điện thoại ngay lập tức.
 * - Ảnh đại diện: form multipart -> upload lên Cloudinary -> lưu secure_url vào users.images.
 * - Email mới: KHÔNG đổi ngay, lưu vào pendingEmail và gửi OTP đến email mới;
 *   người dùng nhập OTP ở /profile/verify-email thì email mới chính thức được cập nhật.
 */
@WebServlet(urlPatterns = { "/profile" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 6)
public class ProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    static final String VIEW = "/views/web/profile.jsp";
    private static final long MAX_AVATAR = 5 * FileValidator.MB;

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = userService.findById((Integer) session.getAttribute("userId"));
        if (user == null) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        moveFlash(session, "profileFlash", req, "success");
        moveFlash(session, "profileFlashError", req, "error");
        req.setAttribute("user", user);
        req.setAttribute("form", new ProfileForm(user.getFullname(), user.getPhone(), user.getEmail()));
        req.setAttribute("cloudinaryReady", CloudinaryUtil.isConfigured());
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
        User user = userService.findById(userId);
        if (user == null) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<String, String> errors = new LinkedHashMap<>();

        // 1) Lấy file ảnh (multipart). Vượt @MultipartConfig -> IllegalStateException
        Part avatar = null;
        try {
            avatar = req.getPart("avatar");
        } catch (IllegalStateException | ServletException e) {
            errors.put("avatar", "Ảnh đại diện tối đa " + MAX_AVATAR / FileValidator.MB + "MB");
        }

        // 2) Validation dữ liệu
        ProfileForm form = new ProfileForm(req.getParameter("fullname"), req.getParameter("phone"), req.getParameter("email"));
        errors.putAll(ValidationUtil.validate(form));
        String fileError = FileValidator.check(avatar, FileValidator.IMAGE_EXT, "image/", MAX_AVATAR, "Ảnh đại diện");
        if (fileError != null) {
            errors.putIfAbsent("avatar", fileError);
        }
        boolean emailChanged = form.getEmail() != null && !form.getEmail().equals(user.getEmail());
        if (emailChanged && !errors.containsKey("email")) {
            try {
                userService.checkEmailAvailable(userId, form.getEmail());
            } catch (FieldValidationException e) {
                errors.put(e.getField(), e.getMessage());
            }
        }
        if (!errors.isEmpty()) {
            forward(req, resp, user, form, errors, null);
            return;
        }

        // 3) Upload ảnh lên Cloudinary (nếu có chọn ảnh)
        CloudinaryUtil.UploadResult uploaded = null;
        try {
            uploaded = CloudinaryUtil.uploadAvatar(avatar);
        } catch (IllegalStateException e) {
            errors.put("avatar", e.getMessage());
            forward(req, resp, user, form, errors, null);
            return;
        }

        // 4) Lưu họ tên, SĐT, ảnh
        try {
            String oldPublicId = userService.updateProfile(userId, form.getFullname(), form.getPhone(),
                    uploaded == null ? null : uploaded.url(), uploaded == null ? null : uploaded.publicId());
            CloudinaryUtil.destroyQuietly(oldPublicId); // xoá ảnh cũ trên Cloudinary
        } catch (IllegalArgumentException e) {
            if (uploaded != null) {
                CloudinaryUtil.destroyQuietly(uploaded.publicId());
            }
            if (e instanceof FieldValidationException fe) {
                errors.put(fe.getField(), fe.getMessage());
            }
            forward(req, resp, user, form, errors, e instanceof FieldValidationException ? null : e.getMessage());
            return;
        }
        session.setAttribute("fullname", form.getFullname());
        if (uploaded != null) {
            session.setAttribute("avatar", uploaded.url());
        }

        // 5) Email mới -> gửi OTP đến email mới, chuyển sang trang nhập OTP
        if (emailChanged) {
            try {
                userService.requestEmailChange(userId, form.getEmail());
                session.setAttribute("emailFlash", "Đã lưu hồ sơ. Mã OTP đã được gửi đến email mới, "
                        + "hãy nhập mã để hoàn tất việc đổi email.");
            } catch (FieldValidationException e) {
                errors.put(e.getField(), e.getMessage());
                forward(req, resp, userService.findById(userId), form, errors, null);
                return;
            } catch (IllegalArgumentException | IllegalStateException e) {
                // vd: gửi lại quá nhanh / SMTP lỗi -> vẫn sang trang xác nhận để bấm "Gửi lại mã"
                session.setAttribute("emailFlashError", e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + "/profile/verify-email");
            return;
        }

        session.setAttribute("profileFlash", "Cập nhật hồ sơ thành công.");
        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, User user, ProfileForm form,
            Map<String, String> errors, String error) throws ServletException, IOException {
        req.setAttribute("user", user);
        req.setAttribute("form", form);
        req.setAttribute("errors", errors);
        req.setAttribute("error", error);
        req.setAttribute("cloudinaryReady", CloudinaryUtil.isConfigured());
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher(VIEW).include(req, resp);
    }

    static void moveFlash(HttpSession session, String key, HttpServletRequest req, String attr) {
        Object v = session.getAttribute(key);
        if (v != null) {
            req.setAttribute(attr, v);
            session.removeAttribute(key);
        }
    }
}
