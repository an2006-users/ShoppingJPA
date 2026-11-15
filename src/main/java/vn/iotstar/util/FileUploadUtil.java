package vn.iotstar.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import jakarta.servlet.http.Part;

/** Xử lý lưu file upload, dùng chung cho Add và Edit. */
public class FileUploadUtil {

    private FileUploadUtil() {
    }

    /**
     * Lưu file upload vào Constant.DIR với tên mới theo thời gian.
     *
     * @return tên file đã lưu, hoặc null nếu người dùng không chọn file
     */
    public static String saveFile(Part part) throws IOException {
        if (part == null || part.getSize() <= 0) {
            return null;
        }
        String submitted = part.getSubmittedFileName();
        if (submitted == null || submitted.isEmpty()) {
            return null;
        }

        String original = Paths.get(submitted).getFileName().toString();
        int index = original.lastIndexOf('.');
        String ext = index >= 0 ? original.substring(index + 1).toLowerCase() : "";

        File uploadDir = new File(Constant.DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String newName = System.currentTimeMillis() + (ext.isEmpty() ? "" : "." + ext);
        part.write(new File(uploadDir, newName).getAbsolutePath());
        return newName;
    }
}
