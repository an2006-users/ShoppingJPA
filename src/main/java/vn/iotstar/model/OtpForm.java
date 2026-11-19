package vn.iotstar.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Form chỉ có 1 ô mã OTP (kích hoạt tài khoản, xác nhận email mới). */
public class OtpForm {

    @NotBlank(message = "Vui lòng nhập mã OTP")
    @Pattern(regexp = Rules.OTP, message = "Mã OTP gồm đúng 6 chữ số")
    private String otp;

    public OtpForm() {
    }

    public OtpForm(String otp) {
        this.otp = otp == null ? null : otp.trim();
    }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
