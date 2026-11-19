package vn.iotstar.controller.video;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Video;
import vn.iotstar.model.VideoForm;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.FileUploadUtil;
import vn.iotstar.util.FileValidator;
import vn.iotstar.util.ValidationUtil;

/**
 * Thêm / sửa video bằng form multipart (upload poster + file video).
 * /video/add  : thêm mới
 * /video/edit?id=... : sửa
 */
@WebServlet(urlPatterns = { "/video/add", "/video/edit" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024,        // > 1MB thì ghi tạm ra đĩa
        maxFileSize = 1024 * 1024 * 200,                 // mỗi file tối đa 200MB
        maxRequestSize = 1024 * 1024 * 210)              // cả request tối đa 210MB
public class VideoSaveController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final long MAX_POSTER = 5 * FileValidator.MB;
    private static final long MAX_VIDEO = 200 * FileValidator.MB;
    private static final String FORM = "/views/web/video-form.jsp";

    private final IVideoService videoService = new VideoServiceImpl();

    private static boolean isEdit(HttpServletRequest req) {
        return req.getServletPath().endsWith("/edit");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (isEdit(req)) {
            int userId = (Integer) req.getSession().getAttribute("userId");
            Video video = videoService.findOwned(req.getParameter("id"), userId);
            if (video == null) {
                resp.sendRedirect(req.getContextPath() + "/video?msg=notfound");
                return;
            }
            req.setAttribute("video", video);
        }
        req.setAttribute("editMode", isEdit(req));
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher(FORM).include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        boolean edit = isEdit(req);
        int userId = (Integer) req.getSession().getAttribute("userId");

        Video video;
        if (edit) {
            // Lấy video hiện có (đã kiểm tra quyền sở hữu) rồi ghi đè các trường được sửa
            video = videoService.findOwned(req.getParameter("id"), userId);
            if (video == null) {
                resp.sendRedirect(req.getContextPath() + "/video?msg=notfound");
                return;
            }
        } else {
            video = new Video();
        }

        String oldPoster = video.getPoster();
        String oldVideoFile = video.getVideoFile();
        String newPoster = null;
        String newVideoFile = null;
        Map<String, String> errors = new LinkedHashMap<>();

        // Gọi getPart trước: nếu file vượt giới hạn @MultipartConfig sẽ ném lỗi tại đây
        Part posterPart = null;
        Part videoPart = null;
        try {
            posterPart = req.getPart("poster");
            videoPart = req.getPart("videoFile");
        } catch (IllegalStateException | ServletException e) {
            errors.put("videoFile", "Upload thất bại: file quá lớn (video tối đa 200MB, poster tối đa 5MB)");
        }

        // 1) Validation dữ liệu chữ bằng Bean Validation
        VideoForm form = new VideoForm(req.getParameter("title"), req.getParameter("description"));
        errors.putAll(ValidationUtil.validate(form));
        video.setTitle(form.getTitle());
        video.setDescription(form.getDescription());
        video.setActive(req.getParameter("active") != null);

        // 2) Validation file: định dạng, kiểu MIME, dung lượng; thêm mới bắt buộc có video
        String posterErr = FileValidator.check(posterPart, FileValidator.IMAGE_EXT, "image/", MAX_POSTER, "Ảnh poster");
        if (posterErr != null) {
            errors.putIfAbsent("poster", posterErr);
        }
        String videoErr = FileValidator.check(videoPart, FileValidator.VIDEO_EXT, null, MAX_VIDEO, "File video");
        if (videoErr != null) {
            errors.putIfAbsent("videoFile", videoErr);
        }
        if (!edit && !errors.containsKey("videoFile") && !FileValidator.hasFile(videoPart)) {
            errors.put("videoFile", "Vui lòng chọn file video để upload");
        }

        if (!errors.isEmpty()) {
            forwardForm(req, resp, video, edit, errors, null);
            return;
        }

        try {
            newPoster = FileUploadUtil.saveFile(posterPart, Constant.VIDEO_DIR, FileValidator.IMAGE_EXT);
            newVideoFile = FileUploadUtil.saveFile(videoPart, Constant.VIDEO_DIR, FileValidator.VIDEO_EXT);
            if (newPoster != null) {
                video.setPoster(newPoster);
            }
            if (newVideoFile != null) {
                video.setVideoFile(newVideoFile);
            }

            if (edit) {
                videoService.update(video, userId);
            } else {
                videoService.insert(video, userId);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Lưu thất bại: xoá file vừa upload để khỏi rác
            FileUploadUtil.deleteQuietly(Constant.VIDEO_DIR, newPoster);
            FileUploadUtil.deleteQuietly(Constant.VIDEO_DIR, newVideoFile);
            video.setPoster(oldPoster);
            video.setVideoFile(oldVideoFile);
            String message = e instanceof IllegalArgumentException ? e.getMessage()
                    : "Không lưu được video. Vui lòng thử lại.";
            forwardForm(req, resp, video, edit, errors, message);
            return;
        }

        // Thành công: xoá file cũ đã bị thay thế
        if (newPoster != null) {
            FileUploadUtil.deleteQuietly(Constant.VIDEO_DIR, oldPoster);
        }
        if (newVideoFile != null) {
            FileUploadUtil.deleteQuietly(Constant.VIDEO_DIR, oldVideoFile);
        }
        resp.sendRedirect(req.getContextPath() + "/video?msg=" + (edit ? "updated" : "added"));
    }

    private void forwardForm(HttpServletRequest req, HttpServletResponse resp, Video video, boolean edit,
            Map<String, String> errors, String error) throws ServletException, IOException {
        req.setAttribute("errors", errors);
        req.setAttribute("error", error);
        req.setAttribute("video", video);
        req.setAttribute("editMode", edit);
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher(FORM).include(req, resp);
    }
}
