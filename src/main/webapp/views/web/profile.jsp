<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%-- Trang nội dung: layout (navbar, footer, Bootstrap) do SiteMesh decorator web.jsp thêm vào --%>
<html>
<head><title>Hồ sơ của tôi</title></head>
<body>
<div class="mx-auto" style="max-width: 960px">
    <h1 class="h3 mb-1">Hồ sơ của tôi</h1>
    <p class="text-secondary">Cập nhật thông tin cá nhân và ảnh đại diện.</p>

    <c:if test="${not empty success}"><div class="alert alert-success"><i class="bi bi-check-circle me-1"></i><c:out value="${success}"/></div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>
    <c:if test="${not empty errors}"><div class="alert alert-danger">Vui lòng kiểm tra lại các ô được đánh dấu đỏ.</div></c:if>

    <c:if test="${not empty user.pendingEmail}">
        <div class="alert alert-warning d-flex flex-wrap align-items-center justify-content-between gap-2">
            <span><i class="bi bi-envelope-exclamation me-1"></i>
                Email mới <strong><c:out value="${user.pendingEmail}"/></strong> đang chờ xác nhận OTP.</span>
            <a class="btn btn-sm btn-warning" href="<c:url value='/profile/verify-email'/>">Nhập mã OTP</a>
        </div>
    </c:if>

    <%-- multipart/form-data: bắt buộc để gửi file ảnh lên server (rồi server upload lên Cloudinary) --%>
    <form class="needs-validation" novalidate action="<c:url value='/profile'/>" method="post" enctype="multipart/form-data">
        <div class="row g-4">
            <div class="col-md-4">
                <div class="card border-0 shadow-sm text-center">
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty user.images}">
                                <img id="avatarPreview" class="avatar avatar-lg mb-3" src="<c:out value='${user.images}'/>" alt="Ảnh đại diện">
                            </c:when>
                            <c:otherwise>
                                <span class="avatar avatar-lg avatar-letter mb-3 mx-auto" id="avatarLetter">${fn:toUpperCase(fn:substring(user.fullname, 0, 1))}</span>
                                <img id="avatarPreview" class="avatar avatar-lg mb-3 d-none" alt="Ảnh đại diện">
                            </c:otherwise>
                        </c:choose>
                        <h2 class="h5 mb-0"><c:out value="${user.fullname}"/></h2>
                        <div class="text-secondary small mb-3">@<c:out value="${user.username}"/></div>

                        <div class="mb-3 text-start">
                            <label class="form-label" for="avatar">Đổi ảnh đại diện</label>
                            <input class="form-control${not empty errors.avatar ? ' is-invalid' : ''}" id="avatar" type="file" name="avatar"
                                   accept=".jpg,.jpeg,.png,.gif,.webp" data-ext="jpg,jpeg,png,gif,webp" data-max-mb="5" data-preview="#avatarPreview" data-preview-hide="#avatarLetter">
                            <div class="form-text">JPG, PNG, GIF, WEBP • tối đa 5MB • lưu trên Cloudinary</div>
                            <div class="invalid-feedback"><c:out value="${not empty errors.avatar ? errors.avatar : 'Ảnh không hợp lệ'}"/></div>
                        </div>
                        <c:if test="${not cloudinaryReady}">
                            <div class="alert alert-secondary small text-start mb-0">
                                Chưa cấu hình Cloudinary nên chưa đổi được ảnh. Xem <code>cloudinary.properties</code>.
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>

            <div class="col-md-8">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4">
                        <div class="mb-3">
                            <label class="form-label" for="username">Tên đăng nhập</label>
                            <input class="form-control" id="username" value="<c:out value='${user.username}'/>" disabled>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="fullname">Họ và tên *</label>
                            <input class="form-control${not empty errors.fullname ? ' is-invalid' : ''}" id="fullname" name="fullname"
                                   required minlength="2" maxlength="100" autocomplete="name" value="<c:out value='${form.fullname}'/>">
                            <div class="invalid-feedback"><c:out value="${not empty errors.fullname ? errors.fullname : 'Họ tên từ 2 đến 100 ký tự'}"/></div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="phone">Số điện thoại</label>
                            <input class="form-control${not empty errors.phone ? ' is-invalid' : ''}" id="phone" name="phone" type="tel"
                                   inputmode="tel" autocomplete="tel" maxlength="15" pattern="(0|\+84)(3|5|7|8|9)[0-9]{8}"
                                   placeholder="0912345678" value="<c:out value='${form.phone}'/>">
                            <div class="invalid-feedback"><c:out value="${not empty errors.phone ? errors.phone : 'Số điện thoại không hợp lệ (vd: 0912345678 hoặc +84912345678)'}"/></div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="email">Email *</label>
                            <input class="form-control${not empty errors.email ? ' is-invalid' : ''}" id="email" name="email" type="email"
                                   required maxlength="100" pattern=".+@.+\..+" autocomplete="email" value="<c:out value='${form.email}'/>">
                            <div class="form-text">Đổi email: hệ thống gửi mã OTP đến <strong>email mới</strong>, nhập đúng mã thì email mới được cập nhật.</div>
                            <div class="invalid-feedback"><c:out value="${not empty errors.email ? errors.email : 'Email không đúng định dạng'}"/></div>
                        </div>
                        <div class="d-flex gap-2">
                            <button class="btn btn-primary" type="submit"><i class="bi bi-save me-1"></i>Lưu thay đổi</button>
                            <a class="btn btn-outline-secondary" href="<c:url value='/profile'/>">Huỷ</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>
</body>
</html>
