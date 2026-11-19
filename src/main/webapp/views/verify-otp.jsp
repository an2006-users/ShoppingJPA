<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head><title>Kích hoạt tài khoản</title></head>
<body>
<div class="card shadow-sm border-0 mx-auto auth-card">
    <div class="card-body p-4">
        <h1 class="h4 mb-1 text-center">Kích hoạt tài khoản</h1>
        <p class="text-secondary text-center mb-4">Nhập mã OTP gồm 6 chữ số đã gửi đến <strong><c:out value="${maskedEmail}"/></strong>.</p>

        <c:if test="${not empty info}"><div class="alert alert-info"><c:out value="${info}"/></div></c:if>
        <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>

        <form class="needs-validation" novalidate action="<c:url value='/verify-otp'/>" method="post">
            <div class="mb-3">
                <label class="form-label" for="otp">Mã OTP</label>
                <input class="form-control otp-input${not empty errors.otp ? ' is-invalid' : ''}" id="otp" name="otp" inputmode="numeric" maxlength="6"
                       pattern="[0-9]{6}" autocomplete="one-time-code" required autofocus>
                <div class="invalid-feedback"><c:out value="${not empty errors.otp ? errors.otp : 'Mã OTP gồm đúng 6 chữ số'}"/></div>
            </div>
            <button class="btn btn-primary w-100" type="submit">Kích hoạt</button>
        </form>
        <div class="text-center small mt-3">
            Chưa nhận được mã?
            <form class="d-inline" action="<c:url value='/verify-otp'/>" method="post">
                <input type="hidden" name="action" value="resend">
                <button type="submit" class="btn btn-link btn-sm p-0 align-baseline">Gửi lại mã</button>
            </form>
            • <a href="<c:url value='/login'/>">Quay lại đăng nhập</a>
        </div>
    </div>
</div>
</body>
</html>
