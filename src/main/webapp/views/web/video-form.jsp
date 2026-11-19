<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head><title>${editMode ? 'Sửa video' : 'Thêm video'}</title></head>
<body>
<div class="mx-auto" style="max-width: 760px">
    <h1 class="h3 mb-3">${editMode ? 'Sửa video' : 'Thêm video'}</h1>

    <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>

    <%-- enctype multipart/form-data bắt buộc để upload file --%>
    <form class="card card-body shadow-sm border-0 needs-validation" novalidate method="post" enctype="multipart/form-data"
          action="<c:url value='${editMode ? "/video/edit" : "/video/add"}'/>">
        <c:if test="${editMode}"><input type="hidden" name="id" value="<c:out value='${video.videoId}'/>"></c:if>

        <div class="mb-3">
            <label class="form-label" for="title">Tiêu đề *</label>
            <input class="form-control${not empty errors.title ? ' is-invalid' : ''}" id="title" type="text" name="title" minlength="3" maxlength="255" required autofocus
                   value="<c:out value='${video.title}'/>">
            <div class="invalid-feedback"><c:out value="${not empty errors.title ? errors.title : 'Tiêu đề từ 3 đến 255 ký tự'}"/></div>
        </div>

        <div class="mb-3">
            <label class="form-label" for="description">Mô tả</label>
            <textarea class="form-control${not empty errors.description ? ' is-invalid' : ''}" id="description" name="description" rows="4" maxlength="2000"><c:out value="${video.description}"/></textarea>
            <div class="invalid-feedback"><c:out value="${not empty errors.description ? errors.description : 'Mô tả tối đa 2000 ký tự'}"/></div>
        </div>

        <div class="mb-3">
            <label class="form-label" for="poster">Ảnh poster</label>
            <c:if test="${not empty video.poster}">
                <div class="d-flex align-items-center gap-3 mb-2">
                    <img class="preview-img" src="<c:url value='/media?fname=${video.poster}'/>" alt="Poster hiện tại">
                    <span class="form-text">Poster hiện tại. Chọn file mới để thay thế.</span>
                </div>
            </c:if>
            <input class="form-control${not empty errors.poster ? ' is-invalid' : ''}" id="poster" type="file" name="poster" accept=".jpg,.jpeg,.png,.gif,.webp"
                   data-ext="jpg,jpeg,png,gif,webp" data-max-mb="5" data-preview="#posterPreview">
            <div class="form-text">jpg, jpeg, png, gif, webp • tối đa 5MB</div>
            <div class="invalid-feedback"><c:out value="${not empty errors.poster ? errors.poster : 'Ảnh poster không hợp lệ'}"/></div>
            <img id="posterPreview" class="preview-img mt-2 d-none" alt="Xem trước">
        </div>

        <div class="mb-3">
            <label class="form-label" for="videoFile">File video <c:if test="${not editMode}">*</c:if></label>
            <c:if test="${not empty video.videoFile}">
                <div class="form-text mb-1">Đã có video: <c:out value="${video.videoFile}"/>. Chọn file mới để thay thế.</div>
            </c:if>
            <input class="form-control${not empty errors.videoFile ? ' is-invalid' : ''}" id="videoFile" type="file" name="videoFile" accept=".mp4,.webm,.ogg,.mov"
                   data-ext="mp4,webm,ogg,mov" data-max-mb="200" <c:if test="${not editMode}">required</c:if>>
            <div class="form-text">mp4, webm, ogg, mov • tối đa 200MB</div>
            <div class="invalid-feedback"><c:out value="${not empty errors.videoFile ? errors.videoFile : 'Vui lòng chọn file video (mp4, webm, ogg, mov)'}"/></div>
        </div>

        <div class="form-check mb-4">
            <input class="form-check-input" id="active" type="checkbox" name="active" <c:if test="${empty video or video.active}">checked</c:if>>
            <label class="form-check-label" for="active">Hiển thị công khai trên trang chủ</label>
        </div>

        <div class="d-flex gap-2">
            <button class="btn btn-primary" type="submit">${editMode ? 'Lưu thay đổi' : 'Thêm video'}</button>
            <a class="btn btn-outline-secondary" href="<c:url value='/video'/>">Huỷ</a>
        </div>
    </form>
</div>
</body>
</html>
