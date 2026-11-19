package vn.iotstar.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Dữ liệu form đăng ký. */
public class RegisterForm {

    @NotBlank(message = "Vui lòng nhập họ tên")
    @Size(min = 2, max = 100, message = "Họ tên từ {min} đến {max} ký tự")
    private String fullname;

    @NotBlank(message = "Vui lòng nhập email")
    @Email(regexp = Rules.EMAIL, message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email tối đa {max} ký tự")
    private String email;

    @NotBlank(message = "Vui lòng nhập tên đăng nhập")
    @Pattern(regexp = Rules.USERNAME, message = "Tên đăng nhập gồm 3-30 ký tự: chữ không dấu, số hoặc dấu gạch dưới")
    private String username;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    @Pattern(regexp = Rules.PASSWORD, message = "Mật khẩu 6-50 ký tự, có ít nhất 1 chữ cái và 1 chữ số")
    private String password;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu")
    private String confirm;

    public RegisterForm() {
    }

    public RegisterForm(String fullname, String email, String username, String password, String confirm) {
        this.fullname = fullname == null ? null : fullname.trim();
        this.email = email == null ? null : email.trim().toLowerCase();
        this.username = username == null ? null : username.trim();
        this.password = password;
        this.confirm = confirm;
    }

    /** Lỗi hiển thị ở field "confirm" (xem ValidationUtil / JSP). */
    @AssertTrue(message = "Mật khẩu nhập lại không khớp")
    public boolean isConfirmMatched() {
        return password == null || confirm == null || confirm.isEmpty() || password.equals(confirm);
    }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirm() { return confirm; }
    public void setConfirm(String confirm) { this.confirm = confirm; }
}
