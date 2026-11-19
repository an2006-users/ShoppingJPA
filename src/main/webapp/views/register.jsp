<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head><title>Đăng ký</title></head>
<body>
<div class="card shadow-sm border-0 mx-auto auth-card">
    <div class="card-body p-4">
        <h1 class="h4 mb-1 text-center">Tạo tài khoản</h1>
        <p class="text-secondary text-center mb-4">Đăng ký để quản lý cửa hàng của bạn.</p>

        <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>

        <form class="needs-validation" novalidate action="<c:url value='/register'/>" method="post">
            <div class="mb-3">
                <label class="form-label" for="fullname">Họ và tên</label>
                <input class="form-control${not empty errors.fullname ? ' is-invalid' : ''}" id="fullname" name="fullname" autocomplete="name"
                       required minlength="2" maxlength="100" autofocus value="<c:out value='${form.fullname}'/>">
                <div class="invalid-feedback"><c:out value="${not empty errors.fullname ? errors.fullname : 'Họ tên từ 2 đến 100 ký tự'}"/></div>
            </div>
            <div class="mb-3">
                <label class="form-label" for="email">Email</label>
                <input class="form-control${not empty errors.email ? ' is-invalid' : ''}" id="email" type="email" name="email" autocomplete="email"
                       required maxlength="100" pattern=".+@.+\..+" value="<c:out value='${form.email}'/>">
                <div class="invalid-feedback"><c:out value="${not empty errors.email ? errors.email : 'Email không đúng định dạng'}"/></div>
            </div>
            <div class="mb-3">
                <label class="form-label" for="username">Tên đăng nhập</label>
                <input class="form-control${not empty errors.username ? ' is-invalid' : ''}" id="username" name="username" autocomplete="username"
                       required pattern="[A-Za-z0-9_]{3,30}" maxlength="30" value="<c:out value='${form.username}'/>">
                <div class="form-text">3-30 ký tự: chữ không dấu, số hoặc dấu gạch dưới.</div>
                <div class="invalid-feedback"><c:out value="${not empty errors.username ? errors.username : 'Tên đăng nhập gồm 3-30 ký tự: chữ không dấu, số hoặc _'}"/></div>
            </div>
            <div class="mb-3">
                <label class="form-label" for="password">Mật khẩu</label>
                <input class="form-control${not empty errors.password ? ' is-invalid' : ''}" id="password" type="password" name="password" autocomplete="new-password"
                       required pattern="(?=.*[A-Za-z])(?=.*\d).{6,50}" maxlength="50">
                <div class="form-text">6-50 ký tự, có ít nhất 1 chữ cái và 1 chữ số.</div>
                <div class="invalid-feedback"><c:out value="${not empty errors.password ? errors.password : 'Mật khẩu 6-50 ký tự, có ít nhất 1 chữ cái và 1 chữ số'}"/></div>
            </div>
            <div class="mb-3">
                <label class="form-label" for="confirm">Nhập lại mật khẩu</label>
                <input class="form-control${not empty errors.confirm ? ' is-invalid' : ''}" id="confirm" type="password" name="confirm" autocomplete="new-password"
                       required data-match="#password" data-match-msg="Mật khẩu nhập lại không khớp">
                <div class="invalid-feedback"><c:out value="${not empty errors.confirm ? errors.confirm : 'Vui lòng nhập lại mật khẩu'}"/></div>
            </div>
            <button class="btn btn-primary w-100" type="submit">Đăng ký</button>
        </form>
        <p class="text-center small mt-3 mb-0">Đã có tài khoản? <a href="<c:url value='/login'/>">Đăng nhập</a></p>
    </div>
</div>
</body>
</html>
