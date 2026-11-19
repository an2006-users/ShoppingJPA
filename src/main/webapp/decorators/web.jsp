<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%--
    DECORATOR "web" (SiteMesh 3) – template Bootstrap 5 dùng cho trang người dùng.
    Trang nội dung chỉ cần viết <title>, (tuỳ chọn) <head> riêng và <body>;
    SiteMesh chèn chúng vào các thẻ <sitemesh:write property="..."/> bên dưới.
--%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property="title"/> | Shopping</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="<c:url value='/assets/css/app.css'/>">
    <sitemesh:write property="head"/>
</head>
<body class="d-flex flex-column min-vh-100 bg-body-tertiary">

<nav class="navbar navbar-expand-lg bg-white border-bottom shadow-sm sticky-top">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center gap-2 fw-bold" href="<c:url value='/home'/>">
            <span class="brand-mark">S</span> Shopping
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
                aria-controls="mainNav" aria-expanded="false" aria-label="Mở menu">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link" data-nav="/home" href="<c:url value='/home'/>"><i class="bi bi-house"></i> Trang chủ</a></li>
                <c:if test="${not empty sessionScope.username}">
                    <li class="nav-item"><a class="nav-link" data-nav="/video" href="<c:url value='/video'/>"><i class="bi bi-collection-play"></i> Video của tôi</a></li>
                    <li class="nav-item"><a class="nav-link" data-nav="/profile" href="<c:url value='/profile'/>"><i class="bi bi-person-circle"></i> Hồ sơ</a></li>
                    <li class="nav-item"><a class="nav-link" data-nav="/admin" href="<c:url value='/admin/dashboard'/>"><i class="bi bi-speedometer2"></i> Quản trị</a></li>
                </c:if>
            </ul>
            <div class="d-flex align-items-center gap-2">
                <c:choose>
                    <c:when test="${not empty sessionScope.username}">
                        <div class="dropdown">
                            <a class="d-flex align-items-center gap-2 text-decoration-none text-body dropdown-toggle" href="#"
                               data-bs-toggle="dropdown" aria-expanded="false">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.avatar}">
                                        <img class="avatar avatar-sm" src="<c:out value='${sessionScope.avatar}'/>" alt="Ảnh đại diện">
                                    </c:when>
                                    <c:otherwise>
                                        <span class="avatar avatar-sm avatar-letter">${fn:toUpperCase(fn:substring(sessionScope.fullname, 0, 1))}</span>
                                    </c:otherwise>
                                </c:choose>
                                <span class="d-none d-sm-inline"><c:out value="${sessionScope.fullname}"/></span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end shadow">
                                <li><h6 class="dropdown-header">@<c:out value="${sessionScope.username}"/></h6></li>
                                <li><a class="dropdown-item" href="<c:url value='/profile'/>"><i class="bi bi-person-circle me-2"></i>Hồ sơ của tôi</a></li>
                                <li><a class="dropdown-item" href="<c:url value='/video'/>"><i class="bi bi-collection-play me-2"></i>Video của tôi</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="<c:url value='/logout'/>"><i class="bi bi-box-arrow-right me-2"></i>Đăng xuất</a></li>
                            </ul>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-outline-primary btn-sm" href="<c:url value='/login'/>">Đăng nhập</a>
                        <a class="btn btn-primary btn-sm" href="<c:url value='/register'/>">Đăng ký</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>

<main class="flex-grow-1 py-4">
    <div class="container">
        <sitemesh:write property="body"/>
    </div>
</main>

<footer class="border-top bg-white py-3 mt-auto">
    <div class="container d-flex flex-wrap justify-content-between small text-secondary">
        <span>© <span data-year></span> Shopping – Servlet + JPA</span>
        <span>Layout: SiteMesh 3 + Bootstrap 5</span>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<c:url value='/assets/js/app.js'/>"></script>
</body>
</html>
