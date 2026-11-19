package vn.iotstar.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dữ liệu form đăng nhập. */
public class LoginForm {

    @NotBlank(message = "Vui lòng nhập tên đăng nhập")
    @Size(max = 30, message = "Tên đăng nhập tối đa {max} ký tự")
    private String username;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    @Size(max = 50, message = "Mật khẩu tối đa {max} ký tự")
    private String password;

    public LoginForm() {
    }

    public LoginForm(String username, String password) {
        this.username = username == null ? null : username.trim();
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
