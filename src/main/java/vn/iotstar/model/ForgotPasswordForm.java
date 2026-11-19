package vn.iotstar.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dữ liệu form quên mật khẩu (bước 1: nhập email). */
public class ForgotPasswordForm {

    @NotBlank(message = "Vui lòng nhập email")
    @Email(regexp = Rules.EMAIL, message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email tối đa {max} ký tự")
    private String email;

    public ForgotPasswordForm() {
    }

    public ForgotPasswordForm(String email) {
        this.email = email == null ? null : email.trim().toLowerCase();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
