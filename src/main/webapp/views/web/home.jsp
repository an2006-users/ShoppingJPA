<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<html>
<head><title>Trang chủ</title></head>
<body>
<div class="d-flex flex-wrap justify-content-between align-items-end gap-2 mb-4">
    <div>
        <h1 class="h3 mb-1">Video mới nhất</h1>
        <p class="text-secondary mb-0">10 video mới được đăng gần đây nhất.</p>
    </div>
    <c:if test="${not empty sessionScope.username}">
        <a class="btn btn-primary" href="<c:url value='/video/add'/>"><i class="bi bi-plus-lg"></i> Đăng video</a>
    </c:if>
</div>

<c:choose>
    <c:when test="${empty videos}">
        <div class="alert alert-light border text-center">Chưa có video nào.</div>
    </c:when>
    <c:otherwise>
        <div class="row row-cols-1 row-cols-sm-2 row-cols-lg-4 g-3">
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
                                <c:if test="${not empty v.user}"><c:out value="${v.user.fullname}"/> • </c:if>
                                ${v.views} lượt xem<c:if test="${not empty v.createdDateText}"> • ${v.createdDateText}</c:if>
                            </div>
                        </div>
                    </article>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>
</body>
</html>
