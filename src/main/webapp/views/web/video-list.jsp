<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head><title>Video của tôi</title></head>
<body>
<div class="d-flex flex-wrap justify-content-between align-items-end gap-2 mb-4">
    <div>
        <h1 class="h3 mb-1">Video của tôi</h1>
        <p class="text-secondary mb-0">${total} video • Trang ${page}/${totalPages}</p>
    </div>
    <a class="btn btn-primary" href="<c:url value='/video/add'/>"><i class="bi bi-plus-lg"></i> Thêm video</a>
</div>

<c:if test="${not empty success}"><div class="alert alert-success"><c:out value="${success}"/></div></c:if>
<c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>

<c:choose>
    <c:when test="${empty videos}">
        <div class="alert alert-light border text-center">Bạn chưa có video nào. <a href="<c:url value='/video/add'/>">Thêm video đầu tiên</a>.</div>
    </c:when>
    <c:otherwise>
        <div class="row row-cols-1 row-cols-sm-2 row-cols-lg-3 g-3">
            <c:forEach items="${videos}" var="v">
                <div class="col">
                    <article class="card h-100 shadow-sm border-0 overflow-hidden">
                        <a class="thumb" href="<c:url value='/watch?id=${v.videoId}'/>">
                            <c:if test="${not empty v.poster}"><img src="<c:url value='/media?fname=${v.poster}'/>" alt="" loading="lazy"></c:if>
                            <span class="play"><i class="bi bi-play-circle-fill"></i></span>
                        </a>
                        <div class="card-body">
                            <a class="card-title h6 d-block text-decoration-none text-body" href="<c:url value='/watch?id=${v.videoId}'/>"><c:out value="${v.title}"/></a>
                            <div class="small text-secondary">
                                <c:choose>
                                    <c:when test="${v.active}"><span class="badge text-bg-success">Hiển thị</span></c:when>
                                    <c:otherwise><span class="badge text-bg-secondary">Đang ẩn</span></c:otherwise>
                                </c:choose>
                                ${v.views} lượt xem<c:if test="${not empty v.createdDateText}"> • ${v.createdDateText}</c:if>
                            </div>
                        </div>
                        <div class="card-footer bg-white border-0 d-flex gap-2">
                            <a class="btn btn-outline-primary btn-sm" href="<c:url value='/video/edit?id=${v.videoId}'/>"><i class="bi bi-pencil"></i> Sửa</a>
                            <form action="<c:url value='/video/delete'/>" method="post" onsubmit="return confirm('Xoá video này?');">
                                <input type="hidden" name="id" value="${v.videoId}">
                                <button class="btn btn-outline-danger btn-sm" type="submit"><i class="bi bi-trash"></i> Xoá</button>
                            </form>
                        </div>
                    </article>
                </div>
            </c:forEach>
        </div>

        <c:if test="${totalPages > 1}">
            <nav class="mt-4" aria-label="Phân trang">
                <ul class="pagination justify-content-center">
                    <li class="page-item ${page > 1 ? '' : 'disabled'}"><a class="page-link" href="<c:url value='/video?page=${page - 1}'/>">«</a></li>
                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <li class="page-item ${p == page ? 'active' : ''}"><a class="page-link" href="<c:url value='/video?page=${p}'/>">${p}</a></li>
                    </c:forEach>
                    <li class="page-item ${page < totalPages ? '' : 'disabled'}"><a class="page-link" href="<c:url value='/video?page=${page + 1}'/>">»</a></li>
                </ul>
            </nav>
        </c:if>
    </c:otherwise>
</c:choose>
</body>
</html>
