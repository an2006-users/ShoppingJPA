<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head><title>Thêm danh mục</title></head>
<body>
<nav aria-label="breadcrumb">
    <ol class="breadcrumb small">
        <li class="breadcrumb-item"><a href="<c:url value='/admin/category/list'/>">Danh mục</a></li>
        <li class="breadcrumb-item active">Thêm mới</li>
    </ol>
</nav>

<div class="card border-0 shadow-sm" style="max-width: 720px">
    <div class="card-body p-4">
        <h1 class="h4 mb-1">Thêm danh mục</h1>
        <p class="text-secondary">Các trường có dấu * là bắt buộc.</p>

        <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>

        <form class="needs-validation" novalidate action="<c:url value='/admin/category/add'/>" method="post" enctype="multipart/form-data">
            <div class="mb-3">
                <label class="form-label" for="catename">Tên danh mục *</label>
                <input id="catename" class="form-control${not empty errors.catename ? ' is-invalid' : ''}" name="catename" required minlength="2" maxlength="100"
                       placeholder="Ví dụ: Điện thoại, Thời trang..." value="<c:out value='${form.catename}'/>">
                <div class="invalid-feedback"><c:out value="${not empty errors.catename ? errors.catename : 'Tên danh mục từ 2 đến 100 ký tự'}"/></div>
            </div>

            <div class="mb-3">
                <label class="form-label" for="icon">Ảnh đại diện</label>
                <input id="icon" class="form-control${not empty errors.icon ? ' is-invalid' : ''}" type="file" name="icon" accept=".jpg,.jpeg,.png,.gif,.webp"
                       data-ext="jpg,jpeg,png,gif,webp" data-max-mb="2" data-preview="#iconPreview">
                <div class="form-text">Không bắt buộc. JPG, PNG, GIF hoặc WEBP, tối đa 2MB.</div>
                <div class="invalid-feedback"><c:out value="${not empty errors.icon ? errors.icon : 'Ảnh không hợp lệ'}"/></div>
                <img id="iconPreview" class="preview-img mt-2 d-none" alt="Xem trước">
            </div>

            <div class="mb-4">
                <label class="form-label d-block">Trạng thái</label>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="status" id="status1" value="1" ${empty form or form.status != 0 ? 'checked' : ''} required>
                    <label class="form-check-label" for="status1">Hoạt động</label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="status" id="status0" value="0" ${form.status == 0 ? 'checked' : ''}>
                    <label class="form-check-label" for="status0">Khóa</label>
                </div>
                <c:if test="${not empty errors.status}"><div class="text-danger small"><c:out value="${errors.status}"/></div></c:if>
            </div>

            <div class="d-flex flex-wrap gap-2">
                <button type="submit" class="btn btn-primary">Lưu danh mục</button>
                <button type="reset" class="btn btn-outline-secondary">Nhập lại</button>
                <a href="<c:url value='/admin/category/list'/>" class="btn btn-link">← Quay lại danh sách</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
