package vn.iotstar.controller.admin;

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
import vn.iotstar.entity.Category;
import vn.iotstar.model.CategoryForm;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.FieldValidationException;
import vn.iotstar.util.FileUploadUtil;
import vn.iotstar.util.FileValidator;
import vn.iotstar.util.ValidationUtil;

@WebServlet(urlPatterns = { "/admin/category/edit" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
public class CategoryEditController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String FORM = "/views/admin/edit-category.jsp";

    private final ICategoryService cateService = new CategoryServiceImpl();

    private Category load(String idParam) {
        try {
            return cateService.findById(Integer.parseInt(idParam));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Category category = load(req.getParameter("id"));
        if (category == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/category/list");
            return;
        }
        req.setAttribute("category", category);
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher(FORM).include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Map<String, String> errors = new LinkedHashMap<>();

        Part icon = null;
        try {
            icon = req.getPart("icon");
        } catch (IllegalStateException | ServletException e) {
            errors.put("icon", "Ảnh quá lớn (tối đa " + CategoryAddController.MAX_IMAGE / FileValidator.MB + "MB)");
        }

        Category current = load(req.getParameter("id"));
        if (current == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/category/list");
            return;
        }

        CategoryForm form = new CategoryForm(req.getParameter("catename"), req.getParameter("status"));
        errors.putAll(ValidationUtil.validate(form));
        String fileError = FileValidator.check(icon, FileValidator.IMAGE_EXT, "image/",
                CategoryAddController.MAX_IMAGE, "Ảnh đại diện");
        if (fileError != null) {
            errors.putIfAbsent("icon", fileError);
        }

        if (errors.isEmpty()) {
            Category category = new Category();
            category.setCategoryId(current.getCategoryId());
            category.setCategoryname(form.getCatename());
            category.setStatus(form.getStatus());
            String saved = null;
            try {
                // Có chọn ảnh mới -> tên file mới; không chọn -> null (service giữ ảnh cũ)
                saved = FileUploadUtil.saveFile(icon);
                category.setImages(saved);
                cateService.update(category);
                resp.sendRedirect(req.getContextPath() + "/admin/category/list");
                return;
            } catch (FieldValidationException e) {
                FileUploadUtil.deleteQuietly(Constant.DIR, saved);
                errors.put(e.getField(), e.getMessage());
            } catch (IOException | IllegalArgumentException e) {
                FileUploadUtil.deleteQuietly(Constant.DIR, saved);
                req.setAttribute("error", "Không lưu được danh mục: " + e.getMessage());
            }
        }

        // Hiển thị lại đúng dữ liệu người dùng vừa nhập (ảnh vẫn là ảnh hiện tại trong DB)
        if (form.getCatename() != null) {
            current.setCategoryname(form.getCatename());
        }
        if (form.getStatus() != null && (form.getStatus() == 0 || form.getStatus() == 1)) {
            current.setStatus(form.getStatus());
        }
        req.setAttribute("errors", errors);
        req.setAttribute("category", current);
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher(FORM).include(req, resp);
    }
}
