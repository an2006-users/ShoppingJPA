package vn.iotstar.util;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Kiểm tra dữ liệu form bằng Bean Validation (Hibernate Validator).
 * Trả về Map: tên field -> thông báo lỗi đầu tiên của field đó (để JSP hiện dưới ô nhập).
 * Lỗi bỏ trống của ô "confirm" (@NotBlank) được ưu tiên hơn lỗi "không khớp" (@AssertTrue).
 */
public final class ValidationUtil {

    /** ParameterMessageInterpolator: không cần Expression Language, chạy ổn trên mọi Tomcat. */
    private static final ValidatorFactory FACTORY = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory();

    private static final Validator VALIDATOR = FACTORY.getValidator();

    /** Lỗi "bỏ trống" được ưu tiên hiển thị trước các lỗi định dạng / độ dài. */
    private static final Set<Class<? extends Annotation>> REQUIRED = Set.of(NotBlank.class, NotEmpty.class, NotNull.class);

    private ValidationUtil() {
    }

    public static <T> Map<String, String> validate(T bean) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (ConstraintViolation<T> v : VALIDATOR.validate(bean)) {
            String field = v.getPropertyPath().toString();
            // Quy ước: @AssertTrue isConfirmMatched() -> lỗi hiển thị ở ô "confirm"
            if (field.endsWith("Matched")) {
                field = field.substring(0, field.length() - "Matched".length());
            }
            boolean required = REQUIRED.contains(v.getConstraintDescriptor().getAnnotation().annotationType());
            if (required) {
                errors.put(field, v.getMessage());
            } else {
                errors.putIfAbsent(field, v.getMessage());
            }
        }
        return errors;
    }

    /** Lấy tham số request đã trim; null nếu không có. */
    public static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
