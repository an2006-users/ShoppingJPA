<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head><title>Quên mật khẩu</title></head>
<body>
<div class="card shadow-sm border-0 mx-auto auth-card">
    <div class="card-body p-4">
        <h1 class="h4 mb-1 text-center">Quên mật khẩu</h1>
        <p class="text-secondary text-center mb-4">Nhập email đã đăng ký, chúng tôi sẽ gửi mã OTP để đặt lại mật khẩu.</p>

        <c:if test="${not empty info}"><div class="alert alert-info"><c:out value="${info}"/></div></c:if>
        <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>

        <form class="needs-validation" novalidate action="<c:url value='/forgot-password'/>" method="post">
            <div class="mb-3">
                <label class="form-label" for="email">Email</label>
                <input class="form-control${not empty errors.email ? ' is-invalid' : ''}" id="email" type="email" name="email" autocomplete="email"
                       required autofocus maxlength="100" pattern=".+@.+\..+" value="<c:out value='${email}'/>">
                <div class="invalid-feedback"><c:out value="${not empty errors.email ? errors.email : 'Vui lòng nhập email hợp lệ'}"/></div>
            </div>
            <button class="btn btn-primary w-100" type="submit">Gửi mã OTP</button>
        </form>
        <p class="text-center small mt-3 mb-0"><a href="<c:url value='/login'/>">← Quay lại đăng nhập</a></p>
    </div>
</div>
</body>
</html>
