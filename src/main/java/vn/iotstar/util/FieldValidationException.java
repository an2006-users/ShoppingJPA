package vn.iotstar.util;

/**
 * Lỗi nghiệp vụ gắn với một ô nhập cụ thể (vd: "Email đã được sử dụng" -> field email),
 * để controller đưa vào Map errors và JSP hiển thị ngay dưới ô đó.
 */
public class FieldValidationException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    private final String field;

    public FieldValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
