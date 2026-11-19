<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head><title>Đặt lại mật khẩu</title></head>
<body>
<div class="card shadow-sm border-0 mx-auto auth-card">
    <div class="card-body p-4">
        <h1 class="h4 mb-1 text-center">Đặt lại mật khẩu</h1>
        <p class="text-secondary text-center mb-4">Nhập mã OTP đã gửi đến <strong><c:out value="${maskedEmail}"/></strong> và mật khẩu mới.</p>

        <c:if test="${not empty info}"><div class="alert alert-info"><c:out value="${info}"/></div></c:if>
        <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>

        <form class="needs-validation" novalidate action="<c:url value='/reset-password'/>" method="post">
            <div class="mb-3">
                <label class="form-label" for="otp">Mã OTP</label>
                <input class="form-control otp-input${not empty errors.otp ? ' is-invalid' : ''}" id="otp" name="otp" inputmode="numeric" maxlength="6"
                       pattern="[0-9]{6}" autocomplete="one-time-code" required autofocus value="<c:out value='${otp}'/>">
                <div class="invalid-feedback"><c:out value="${not empty errors.otp ? errors.otp : 'Mã OTP gồm đúng 6 chữ số'}"/></div>
            </div>
            <div class="mb-3">
                <label class="form-label" for="password">Mật khẩu mới</label>
                <input class="form-control${not empty errors.password ? ' is-invalid' : ''}" id="password" type="password" name="password" autocomplete="new-password"
                       required pattern="(?=.*[A-Za-z])(?=.*\d).{6,50}" maxlength="50">
                <div class="invalid-feedback"><c:out value="${not empty errors.password ? errors.password : 'Mật khẩu 6-50 ký tự, có ít nhất 1 chữ cái và 1 chữ số'}"/></div>
            </div>
            <div class="mb-3">
                <label class="form-label" for="confirm">Nhập lại mật khẩu mới</label>
                <input class="form-control${not empty errors.confirm ? ' is-invalid' : ''}" id="confirm" type="password" name="confirm" autocomplete="new-password"
                       required data-match="#password" data-match-msg="Mật khẩu nhập lại không khớp">
                <div class="invalid-feedback"><c:out value="${not empty errors.confirm ? errors.confirm : 'Vui lòng nhập lại mật khẩu mới'}"/></div>
            </div>
            <button class="btn btn-primary w-100" type="submit">Đổi mật khẩu</button>
        </form>
        <div class="text-center small mt-3">
            Chưa nhận được mã?
            <form class="d-inline" action="<c:url value='/reset-password'/>" method="post">
                <input type="hidden" name="action" value="resend">
                <button type="submit" class="btn btn-link btn-sm p-0 align-baseline">Gửi lại mã</button>
            </form>
            • <a href="<c:url value='/login'/>">Quay lại đăng nhập</a>
        </div>
    </div>
</div>
</body>
</html>
