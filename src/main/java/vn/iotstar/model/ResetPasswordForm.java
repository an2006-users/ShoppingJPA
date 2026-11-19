package vn.iotstar.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Form quên mật khẩu bước 2: OTP + mật khẩu mới. */
public class ResetPasswordForm {

    @NotBlank(message = "Vui lòng nhập mã OTP")
    @Pattern(regexp = Rules.OTP, message = "Mã OTP gồm đúng 6 chữ số")
    private String otp;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    @Pattern(regexp = Rules.PASSWORD, message = "Mật khẩu 6-50 ký tự, có ít nhất 1 chữ cái và 1 chữ số")
    private String password;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu mới")
    private String confirm;

    public ResetPasswordForm() {
    }

    public ResetPasswordForm(String otp, String password, String confirm) {
        this.otp = otp == null ? null : otp.trim();
        this.password = password;
        this.confirm = confirm;
    }

    @AssertTrue(message = "Mật khẩu nhập lại không khớp")
    public boolean isConfirmMatched() {
        return password == null || confirm == null || confirm.isEmpty() || password.equals(confirm);
    }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirm() { return confirm; }
    public void setConfirm(String confirm) { this.confirm = confirm; }
}
