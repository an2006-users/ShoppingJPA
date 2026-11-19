package vn.iotstar.util;

import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import jakarta.servlet.http.Part;

/** Kiểm tra file upload (phần mở rộng, kiểu MIME, dung lượng) trước khi lưu. */
public final class FileValidator {

    public static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");
    public static final Set<String> VIDEO_EXT = Set.of("mp4", "webm", "ogg", "mov");

    public static final long MB = 1024L * 1024L;

    private FileValidator() {
    }

    /** true nếu người dùng thật sự chọn file. */
    public static boolean hasFile(Part part) {
        return part != null && part.getSize() > 0
                && part.getSubmittedFileName() != null && !part.getSubmittedFileName().isBlank();
    }

    /**
     * @param mimePrefix ví dụ "image/" (null = không kiểm tra MIME)
     * @return thông báo lỗi, hoặc null nếu hợp lệ / không chọn file
     */
    public static String check(Part part, Set<String> allowedExt, String mimePrefix, long maxBytes, String label) {
        if (!hasFile(part)) {
            return null;
        }
        String name = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        int dot = name.lastIndexOf('.');
        String ext = dot >= 0 ? name.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
        if (!allowedExt.contains(ext)) {
            return label + " chỉ chấp nhận định dạng: " + String.join(", ", new TreeSet<>(allowedExt));
        }
        String type = part.getContentType();
        if (mimePrefix != null && type != null && !type.toLowerCase(Locale.ROOT).startsWith(mimePrefix)) {
            return label + " không đúng kiểu file";
        }
        if (part.getSize() > maxBytes) {
            return label + " tối đa " + (maxBytes / MB) + "MB";
        }
        return null;
    }
}
