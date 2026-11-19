package vn.iotstar.model;

/** Biểu thức chính quy dùng chung cho các form (giống thuộc tính pattern ở JSP). */
public final class Rules {

    public static final String USERNAME = "^[A-Za-z0-9_]{3,30}$";
    public static final String EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    /** 6-50 ký tự, có ít nhất 1 chữ cái và 1 chữ số. */
    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d).{6,50}$";
    public static final String OTP = "^\\d{6}$";
    /** Số điện thoại Việt Nam: 0xxxxxxxxx hoặc +84xxxxxxxxx (đầu số 3,5,7,8,9). */
    public static final String PHONE = "^(0|\\+84)(3|5|7|8|9)\\d{8}$";

    private Rules() {
    }
}
