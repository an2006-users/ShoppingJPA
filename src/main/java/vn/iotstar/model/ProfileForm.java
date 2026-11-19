package vn.iotstar.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Form cập nhật hồ sơ (ảnh đại diện được kiểm tra riêng bằng FileValidator). */
public class ProfileForm {

    @NotBlank(message = "Vui lòng nhập họ tên")
    @Size(min = 2, max = 100, message = "Họ tên từ {min} đến {max} ký tự")
    private String fullname;

    /** Không bắt buộc; nếu nhập phải là số điện thoại Việt Nam hợp lệ. */
    @Pattern(regexp = "^$|" + Rules.PHONE, message = "Số điện thoại không hợp lệ (vd: 0912345678 hoặc +84912345678)")
    private String phone;

    @NotBlank(message = "Vui lòng nhập email")
    @Email(regexp = Rules.EMAIL, message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email tối đa {max} ký tự")
    private String email;

    public ProfileForm() {
    }

    public ProfileForm(String fullname, String phone, String email) {
        this.fullname = fullname == null ? null : fullname.trim();
        // bỏ khoảng trắng / dấu chấm / gạch người dùng hay gõ: 0912 345 678 -> 0912345678
        this.phone = phone == null ? "" : phone.replaceAll("[\\s.\\-]", "");
        this.email = email == null ? null : email.trim().toLowerCase();
    }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
