package vn.iotstar.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Form thêm / sửa video (poster + file video được kiểm tra riêng bằng FileValidator). */
public class VideoForm {

    @NotBlank(message = "Tiêu đề video không được để trống")
    @Size(min = 3, max = 255, message = "Tiêu đề từ {min} đến {max} ký tự")
    private String title;

    @Size(max = 2000, message = "Mô tả tối đa {max} ký tự")
    private String description;

    public VideoForm() {
    }

    public VideoForm(String title, String description) {
        this.title = title == null ? null : title.trim();
        this.description = description == null || description.isBlank() ? null : description.trim();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
