package vn.iotstar.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.http.Part;

/**
 * Upload / xoá ảnh trên Cloudinary.
 * Cấu hình: biến môi trường CLOUDINARY_URL, hoặc CLOUDINARY_CLOUD_NAME / _API_KEY / _API_SECRET,
 * hoặc file src/main/resources/cloudinary.properties.
 */
public final class CloudinaryUtil {

    /** Kết quả upload: URL https để hiển thị và public_id để xoá sau này. */
    public record UploadResult(String url, String publicId) {
    }

    private static final Properties CONFIG = load();
    private static volatile Cloudinary cloudinary;

    private CloudinaryUtil() {
    }

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = CloudinaryUtil.class.getClassLoader().getResourceAsStream("cloudinary.properties")) {
            if (in != null) {
                p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        override(p, "cloudinary.cloud_name", "CLOUDINARY_CLOUD_NAME");
        override(p, "cloudinary.api_key", "CLOUDINARY_API_KEY");
        override(p, "cloudinary.api_secret", "CLOUDINARY_API_SECRET");
        override(p, "cloudinary.url", "CLOUDINARY_URL");
        return p;
    }

    private static void override(Properties p, String key, String env) {
        String v = System.getenv(env);
        if (v != null && !v.isBlank()) {
            p.setProperty(key, v.trim());
        }
    }

    private static String prop(String key) {
        String v = CONFIG.getProperty(key, "").trim();
        return v.startsWith("your_") ? "" : v;
    }

    public static boolean isConfigured() {
        return !prop("cloudinary.url").isEmpty()
                || (!prop("cloudinary.cloud_name").isEmpty() && !prop("cloudinary.api_key").isEmpty()
                        && !prop("cloudinary.api_secret").isEmpty());
    }

    private static Cloudinary client() {
        if (!isConfigured()) {
            throw new IllegalStateException("Chưa cấu hình Cloudinary (xem src/main/resources/cloudinary.properties)");
        }
        if (cloudinary == null) {
            synchronized (CloudinaryUtil.class) {
                if (cloudinary == null) {
                    String url = prop("cloudinary.url");
                    cloudinary = !url.isEmpty() ? new Cloudinary(url)
                            : new Cloudinary(ObjectUtils.asMap(
                                    "cloud_name", prop("cloudinary.cloud_name"),
                                    "api_key", prop("cloudinary.api_key"),
                                    "api_secret", prop("cloudinary.api_secret"),
                                    "secure", true));
                }
            }
        }
        return cloudinary;
    }

    /**
     * Upload ảnh (Part của form multipart) lên Cloudinary, cắt vuông 400x400 theo khuôn mặt.
     *
     * @return null nếu người dùng không chọn file
     * @throws IllegalStateException chưa cấu hình hoặc Cloudinary trả lỗi
     */
    public static UploadResult uploadAvatar(Part part) {
        if (!FileValidator.hasFile(part)) {
            return null;
        }
        try (InputStream in = part.getInputStream()) {
            byte[] bytes = in.readAllBytes();
            Map<?, ?> result = client().uploader().upload(bytes, ObjectUtils.asMap(
                    "folder", CONFIG.getProperty("cloudinary.folder", "shopping/avatars"),
                    "resource_type", "image",
                    "transformation", "c_fill,g_face,w_400,h_400,q_auto,f_auto"));
            return new UploadResult((String) result.get("secure_url"), (String) result.get("public_id"));
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            if (e instanceof IllegalStateException ise) {
                throw ise;
            }
            throw new IllegalStateException("Upload ảnh lên Cloudinary thất bại: " + e.getMessage(), e);
        }
    }

    /** Xoá ảnh cũ trên Cloudinary; lỗi chỉ ghi log, không làm hỏng thao tác chính. */
    public static void destroyQuietly(String publicId) {
        if (publicId == null || publicId.isBlank() || !isConfigured()) {
            return;
        }
        try {
            client().uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
