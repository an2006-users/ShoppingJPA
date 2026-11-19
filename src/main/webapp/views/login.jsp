<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head><title>Đăng nhập</title></head>
<body>
<div class="card shadow-sm border-0 mx-auto auth-card">
    <div class="card-body p-4">
        <h1 class="h4 mb-1 text-center">Đăng nhập</h1>
        <p class="text-secondary text-center mb-4">Đăng nhập để tiếp tục quản lý cửa hàng.</p>

        <c:if test="${not empty success}"><div class="alert alert-success"><c:out value="${success}"/></div></c:if>
        <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>

        <form class="needs-validation" novalidate action="<c:url value='/login'/>" method="post">
            <div class="mb-3">
                <label class="form-label" for="username">Tên đăng nhập</label>
                <input class="form-control${not empty errors.username ? ' is-invalid' : ''}" id="username" name="username" autocomplete="username"
                       required maxlength="30" autofocus value="<c:out value='${username}'/>">
                <div class="invalid-feedback"><c:out value="${not empty errors.username ? errors.username : 'Vui lòng nhập tên đăng nhập'}"/></div>
            </div>
            <div class="mb-2">
                <label class="form-label" for="password">Mật khẩu</label>
                <input class="form-control${not empty errors.password ? ' is-invalid' : ''}" id="password" type="password" name="password"
                       autocomplete="current-password" required maxlength="50">
                <div class="invalid-feedback"><c:out value="${not empty errors.password ? errors.password : 'Vui lòng nhập mật khẩu'}"/></div>
            </div>
            <div class="text-end mb-3"><a class="small" href="<c:url value='/forgot-password'/>">Quên mật khẩu?</a></div>
            <button class="btn btn-primary w-100" type="submit">Đăng nhập</button>
        </form>
        <p class="text-center small mt-3 mb-0">Chưa có tài khoản? <a href="<c:url value='/register'/>">Đăng ký ngay</a></p>
    </div>
</div>
</body>
</html>
