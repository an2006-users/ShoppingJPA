<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head><title>Dashboard</title></head>
<body>
<nav aria-label="breadcrumb">
    <ol class="breadcrumb small"><li class="breadcrumb-item">Trang quản trị</li><li class="breadcrumb-item active">Dashboard</li></ol>
</nav>

<div class="d-flex flex-wrap justify-content-between align-items-end gap-2 mb-4">
    <div>
        <h1 class="h3 mb-1">Chào mừng trở lại!</h1>
        <p class="text-secondary mb-0">Đây là tình hình tổng quan của cửa hàng hôm nay.</p>
    </div>
    <a class="btn btn-primary" href="<c:url value='/admin/category/add'/>"><i class="bi bi-plus-lg"></i> Thêm danh mục</a>
</div>

<div class="row g-3 mb-4">
    <div class="col-sm-6 col-xl-3">
        <div class="card border-0 shadow-sm"><div class="card-body">
            <div class="text-secondary small">Tổng danh mục</div>
            <div class="fs-3 fw-semibold">${categoryCount}</div>
        </div></div>
    </div>
    <div class="col-sm-6 col-xl-3">
        <div class="card border-0 shadow-sm"><div class="card-body">
            <div class="text-secondary small">Danh mục có ảnh</div>
            <div class="fs-3 fw-semibold">${imageCount}</div>
        </div></div>
    </div>
</div>

<div class="card border-0 shadow-sm">
    <div class="card-header bg-white d-flex justify-content-between align-items-center">
        <div><h2 class="h6 mb-0">Danh mục gần đây</h2><small class="text-secondary">Các danh mục mới nhất theo mã ID</small></div>
        <a class="small" href="<c:url value='/admin/category/list'/>">Xem tất cả →</a>
    </div>
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light"><tr><th>STT</th><th>Danh mục</th><th>Hình ảnh</th><th class="text-end">Thao tác</th></tr></thead>
            <tbody>
            <c:forEach items="${recentCategories}" var="cate" varStatus="status">
                <tr>
                    <td>${status.index + 1}</td>
                    <td>
                        <div class="d-flex align-items-center gap-2">
                            <c:choose>
                                <c:when test="${not empty cate.images}"><img class="category-img" src="<c:url value='/image?fname=${cate.images}'/>" alt=""></c:when>
                                <c:otherwise><span class="category-img d-inline-grid bg-light text-secondary" style="place-items:center"><i class="bi bi-image"></i></span></c:otherwise>
                            </c:choose>
                            <strong><c:out value="${cate.categoryname}"/></strong>
                        </div>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty cate.images}"><span class="badge text-bg-success">Đã có ảnh</span></c:when>
                            <c:otherwise><span class="badge text-bg-warning">Chưa có ảnh</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td class="text-end"><a class="btn btn-sm btn-outline-primary" href="<c:url value='/admin/category/edit?id=${cate.categoryId}'/>">Chỉnh sửa</a></td>
                </tr>
            </c:forEach>
            <c:if test="${empty recentCategories}">
                <tr><td colspan="4" class="text-center text-secondary py-4">Chưa có danh mục. Hãy tạo danh mục đầu tiên của bạn.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
