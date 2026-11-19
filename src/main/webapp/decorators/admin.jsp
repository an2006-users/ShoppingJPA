<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%--
    DECORATOR "admin" (SiteMesh 3) – cùng template Bootstrap 5 với decorator web,
    thêm sidebar quản trị. Áp dụng cho /admin/* (xem WEB-INF/sitemesh3.xml).
--%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property="title"/> | Shopping Admin</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
    <sitemesh:write property="head"/>
</head>
<body class="bg-body-tertiary">

<nav class="navbar navbar-dark bg-dark sticky-top shadow-sm">
    <div class="container-fluid">
        <button class="btn btn-outline-light d-lg-none me-2" type="button" data-bs-toggle="offcanvas"
                data-bs-target="#adminSidebar" aria-controls="adminSidebar" aria-label="Mở menu">
            <i class="bi bi-list"></i>
        </button>
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold me-auto" href="<c:url value='/admin/dashboard'/>">
            <span class="brand-mark">S</span> SHOPPING <small class="text-secondary fw-normal">ADMIN</small>
        </a>
        <div class="d-flex align-items-center gap-3">
            <span class="text-white-50 small d-none d-md-inline" data-today></span>
            <c:choose>
                <c:when test="${not empty sessionScope.avatar}">
                    <img class="avatar avatar-sm" src="<c:out value='${sessionScope.avatar}'/>" alt="Ảnh đại diện">
                </c:when>
                <c:otherwise>
                    <span class="avatar avatar-sm avatar-letter">${fn:toUpperCase(fn:substring(sessionScope.fullname, 0, 1))}</span>
                </c:otherwise>
            </c:choose>
            <a class="btn btn-sm btn-outline-light" href="<c:url value='/logout'/>"><i class="bi bi-box-arrow-right"></i> Đăng xuất</a>
        </div>
    </div>
</nav>

<div class="container-fluid">
    <div class="row">
        <aside class="col-lg-2 p-0 bg-white border-end admin-sidebar">
            <div class="offcanvas-lg offcanvas-start" tabindex="-1" id="adminSidebar" aria-labelledby="adminSidebarLabel">
                <div class="offcanvas-header">
                    <h5 class="offcanvas-title" id="adminSidebarLabel">Menu</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="offcanvas" data-bs-target="#adminSidebar" aria-label="Đóng"></button>
                </div>
                <div class="offcanvas-body d-flex flex-column p-3">
                    <div class="text-uppercase small text-secondary fw-semibold mb-2">Menu chính</div>
                    <ul class="nav nav-pills flex-column gap-1">
                        <li class="nav-item"><a class="nav-link" data-nav="/admin/dashboard" href="<c:url value='/admin/dashboard'/>"><i class="bi bi-speedometer2 me-2"></i>Dashboard</a></li>
                        <li class="nav-item"><a class="nav-link" data-nav="/admin/category" href="<c:url value='/admin/category/list'/>"><i class="bi bi-tags me-2"></i>Quản lý danh mục</a></li>
                        <li class="nav-item"><a class="nav-link" data-nav="/video" href="<c:url value='/video'/>"><i class="bi bi-collection-play me-2"></i>Video của tôi</a></li>
                        <li class="nav-item"><a class="nav-link" href="<c:url value='/profile'/>"><i class="bi bi-person-circle me-2"></i>Hồ sơ của tôi</a></li>
                        <li class="nav-item"><a class="nav-link" href="<c:url value='/home'/>"><i class="bi bi-house me-2"></i>Về trang chủ</a></li>
                    </ul>
                    <div class="mt-auto pt-4 small text-secondary">
                        Đăng nhập: <strong class="text-body"><c:out value="${sessionScope.fullname}"/></strong><br>
                        @<c:out value="${sessionScope.username}"/>
                    </div>
                </div>
            </div>
        </aside>

        <main class="col-lg-10 px-3 px-lg-4 py-4">
            <sitemesh:write property="body"/>
            <footer class="pt-4 mt-4 border-top small text-secondary">
                © <span data-year></span> Shopping Admin • SiteMesh 3 + Bootstrap 5
            </footer>
        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value='/assets/js/app.js'/>"></script>
</body>
</html>
