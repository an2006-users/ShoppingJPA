<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head><title>Quản lý danh mục</title></head>
<body>
<nav aria-label="breadcrumb">
    <ol class="breadcrumb small"><li class="breadcrumb-item">Trang quản trị</li><li class="breadcrumb-item active">Danh mục</li></ol>
</nav>

<h1 class="h3 mb-1">Danh mục sản phẩm</h1>
<p class="text-secondary">Tạo, tìm kiếm, cập nhật và xóa các danh mục của cửa hàng.</p>

<c:if test="${not empty success}"><div class="alert alert-success"><c:out value="${success}"/></div></c:if>

<div class="d-flex flex-wrap justify-content-between gap-2 mb-3">
    <a class="btn btn-primary" href="<c:url value='/admin/category/add'/>"><i class="bi bi-plus-lg"></i> Thêm danh mục</a>
    <form class="d-flex gap-2" action="<c:url value='/admin/category/list'/>" method="get" role="search">
        <input type="search" name="keyword" class="form-control" placeholder="Tìm theo tên danh mục..." value="<c:out value='${keyword}'/>">
        <button type="submit" class="btn btn-outline-secondary text-nowrap"><i class="bi bi-search"></i> Tìm</button>
    </form>
</div>

<div class="card border-0 shadow-sm">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
            <tr><th>STT</th><th>Hình ảnh</th><th>Tên danh mục</th><th>Trạng thái</th><th class="text-end">Thao tác</th></tr>
            </thead>
            <tbody>
            <c:forEach items="${cateList}" var="cate" varStatus="STT">
                <tr>
                    <td>${offset + STT.index + 1}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty cate.images}">
                                <img class="category-img" src="<c:url value='/image?fname=${cate.images}'/>" alt="Ảnh <c:out value='${cate.categoryname}'/>">
                            </c:when>
                            <c:otherwise><span class="category-img d-inline-grid bg-light text-secondary" style="place-items:center"><i class="bi bi-image"></i></span></c:otherwise>
                        </c:choose>
                    </td>
                    <td class="fw-semibold"><c:out value="${cate.categoryname}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${cate.status == 1}"><span class="badge text-bg-success">Hoạt động</span></c:when>
                            <c:otherwise><span class="badge text-bg-secondary">Khóa</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td class="text-end text-nowrap">
                        <a class="btn btn-sm btn-outline-primary" href="<c:url value='/admin/category/edit?id=${cate.categoryId}'/>"><i class="bi bi-pencil"></i> Sửa</a>
                        <a class="btn btn-sm btn-outline-danger" href="<c:url value='/admin/category/delete?id=${cate.categoryId}'/>"
                           onclick="return confirm('Bạn có chắc muốn xóa danh mục này?');"><i class="bi bi-trash"></i> Xóa</a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty cateList}">
                <tr><td colspan="5" class="text-center text-secondary py-4">Không có danh mục nào phù hợp</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<c:if test="${totalPages > 1}">
    <nav class="mt-3" aria-label="Phân trang">
        <ul class="pagination justify-content-center">
            <li class="page-item ${page > 1 ? '' : 'disabled'}"><a class="page-link" href="<c:url value='/admin/category/list?page=${page - 1}'/>">« Trước</a></li>
            <li class="page-item disabled"><span class="page-link">Trang ${page} / ${totalPages}</span></li>
            <li class="page-item ${page < totalPages ? '' : 'disabled'}"><a class="page-link" href="<c:url value='/admin/category/list?page=${page + 1}'/>">Sau »</a></li>
        </ul>
    </nav>
</c:if>
</body>
</html>
