package vn.iotstar.service;

import vn.iotstar.entity.User;

public interface IUserService {

    /**
     * Đăng ký tài khoản mới (mật khẩu truyền vào là mật khẩu gốc, service sẽ băm).
     * Tài khoản được tạo ở trạng thái CHƯA kích hoạt; gọi {@link #sendActivationOtp(String)} để gửi OTP.
     *
     * @throws IllegalArgumentException nếu dữ liệu sai hoặc username / email đã tồn tại
     */
    User register(String username, String password, String fullname, String email);

    /**
     * Trả về User nếu đúng tài khoản và mật khẩu (kể cả chưa kích hoạt - controller phải kiểm tra
     * {@link User#isActive()}), ngược lại trả về null.
     */
    User login(String username, String password);

    /**
     * Sinh OTP kích hoạt và gửi qua email.
     *
     * @throws IllegalArgumentException tài khoản không tồn tại / đã kích hoạt / gửi lại quá nhanh
     * @throws IllegalStateException    gửi email thất bại
     */
    void sendActivationOtp(String email);

    /** @throws IllegalArgumentException OTP sai, hết hạn hoặc nhập sai quá số lần cho phép */
    void activate(String email, String otp);

    /**
     * Quên mật khẩu: gửi OTP đặt lại mật khẩu. Nếu email không tồn tại thì bỏ qua (không báo lỗi)
     * để không lộ email nào đã đăng ký.
     *
     * @throws IllegalArgumentException email sai định dạng / gửi lại quá nhanh
     * @throws IllegalStateException    gửi email thất bại
     */
    void requestPasswordReset(String email);

    /** @throws IllegalArgumentException OTP sai / hết hạn hoặc mật khẩu mới không hợp lệ */
    void resetPassword(String email, String otp, String newPassword);

    // ------------------------------------------------------------------ hồ sơ (Bài 3)

    /** Trả về null nếu không có. */
    User findById(int userId);

    /**
     * Kiểm tra email mới có dùng được không (chưa thuộc tài khoản đã kích hoạt khác).
     *
     * @throws vn.iotstar.util.FieldValidationException field "email"
     */
    void checkEmailAvailable(int userId, String newEmail);

    /**
     * Cập nhật họ tên, số điện thoại và (nếu có) ảnh đại diện mới.
     *
     * @param imageUrl      null = giữ ảnh cũ
     * @return public_id của ảnh cũ đã bị thay thế (để xoá trên Cloudinary), hoặc null
     */
    String updateProfile(int userId, String fullname, String phone, String imageUrl, String imagePublicId);

    /**
     * Đổi email: lưu email mới vào pendingEmail và gửi OTP đến EMAIL MỚI.
     * Email chỉ thực sự đổi khi gọi {@link #confirmEmailChange(int, String)} với OTP đúng.
     *
     * @throws vn.iotstar.util.FieldValidationException email trùng / giống email hiện tại
     * @throws IllegalArgumentException gửi lại quá nhanh
     * @throws IllegalStateException    gửi email thất bại
     */
    void requestEmailChange(int userId, String newEmail);

    /** Gửi lại OTP đến email đang chờ xác nhận. */
    void resendEmailChangeOtp(int userId);

    /**
     * Xác nhận OTP: chuyển pendingEmail thành email chính thức.
     *
     * @return email mới
     * @throws IllegalArgumentException OTP sai / hết hạn / email đã bị người khác dùng
     */
    String confirmEmailChange(int userId, String otp);

    /** Huỷ yêu cầu đổi email đang chờ. */
    void cancelEmailChange(int userId);
}
