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

@WebServlet(urlPatterns = { "/admin/category/add" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
public class CategoryAddController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    static final long MAX_IMAGE = 2 * FileValidator.MB;
    private static final String FORM = "/views/admin/add-category.jsp";

    private final ICategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher(FORM).include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Map<String, String> errors = new LinkedHashMap<>();

        // Gọi getPart trước: file vượt @MultipartConfig sẽ ném IllegalStateException ở đây
        Part icon = null;
        try {
            icon = req.getPart("icon");
        } catch (IllegalStateException | ServletException e) {
            errors.put("icon", "Ảnh quá lớn (tối đa " + MAX_IMAGE / FileValidator.MB + "MB)");
        }

        CategoryForm form = new CategoryForm(req.getParameter("catename"), req.getParameter("status"));
        errors.putAll(ValidationUtil.validate(form));
        String fileError = FileValidator.check(icon, FileValidator.IMAGE_EXT, "image/", MAX_IMAGE, "Ảnh đại diện");
        if (fileError != null) {
            errors.putIfAbsent("icon", fileError);
        }

        if (errors.isEmpty()) {
            Category category = new Category();
            category.setCategoryname(form.getCatename());
            category.setStatus(form.getStatus());
            String saved = null;
            try {
                saved = FileUploadUtil.saveFile(icon); // không chọn ảnh -> null
                category.setImages(saved);
                cateService.insert(category);
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

        req.setAttribute("errors", errors);
        req.setAttribute("form", form);
        // Tomcat 11 + SiteMesh: dùng include() thay forward(); trang include không tự đặt content type
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher(FORM).include(req, resp);
    }
}
