package vn.iotstar.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Form thêm / sửa danh mục (ảnh được kiểm tra riêng bằng FileValidator). */
public class CategoryForm {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 100, message = "Tên danh mục từ {min} đến {max} ký tự")
    private String catename;

    @NotNull(message = "Vui lòng chọn trạng thái")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 1, message = "Trạng thái không hợp lệ")
    private Integer status;

    public CategoryForm() {
    }

    public CategoryForm(String catename, String status) {
        this.catename = catename == null ? null : catename.trim();
        try {
            this.status = status == null ? null : Integer.valueOf(status.trim());
        } catch (NumberFormatException e) {
            this.status = -1; // sẽ bị @Min báo lỗi
        }
    }

    public String getCatename() { return catename; }
    public void setCatename(String catename) { this.catename = catename; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
