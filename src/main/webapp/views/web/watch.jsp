<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head><title><c:choose><c:when test="${not empty video}"><c:out value="${video.title}"/></c:when><c:otherwise>Video</c:otherwise></c:choose></title></head>
<body>
<div class="mx-auto" style="max-width: 960px">
    <c:choose>
        <c:when test="${not empty error}">
            <div class="alert alert-danger"><c:out value="${error}"/></div>
            <a class="btn btn-outline-secondary" href="<c:url value='/home'/>">← Về trang chủ</a>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${not empty video.videoFile}">
                    <video class="w-100 rounded bg-black" controls autoplay preload="metadata"
                           <c:if test="${not empty video.poster}">poster="<c:url value='/media?fname=${video.poster}'/>"</c:if>
                           src="<c:url value='/media?fname=${video.videoFile}'/>">
                        Trình duyệt của bạn không hỗ trợ thẻ video.
                    </video>
                </c:when>
                <c:otherwise><div class="alert alert-light border text-center">Video này chưa có file để phát.</div></c:otherwise>
            </c:choose>

            <h1 class="h4 mt-3"><c:out value="${video.title}"/></h1>
            <div class="small text-secondary">
                <c:if test="${not empty video.user}">Đăng bởi <strong><c:out value="${video.user.fullname}"/></strong> • </c:if>
                ${video.views} lượt xem<c:if test="${not empty video.createdDateText}"> • ${video.createdDateText}</c:if>
                <c:if test="${not video.active}"> • <span class="badge text-bg-secondary">Đang ẩn</span></c:if>
            </div>
            <c:if test="${not empty video.description}"><div class="card card-body mt-3" style="white-space: pre-line"><c:out value="${video.description}"/></div></c:if>

            <div class="d-flex gap-2 mt-4">
                <a class="btn btn-outline-secondary" href="<c:url value='/home'/>">← Trang chủ</a>
                <c:if test="${owner}">
                    <a class="btn btn-outline-secondary" href="<c:url value='/video'/>">Video của tôi</a>
                    <a class="btn btn-primary" href="<c:url value='/video/edit?id=${video.videoId}'/>"><i class="bi bi-pencil"></i> Sửa video</a>
                </c:if>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
