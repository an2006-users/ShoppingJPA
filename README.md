# ShoppingJPA – CRUD Category với JPA + Jakarta EE 11 (Servlet 6.1, Tomcat 11)

Chuyển bài CRUD Category (Servlet + JDBC) sang **JPA (Hibernate 7)**, kiến trúc 3 lớp:
Controller → Service → DAO → Entity (EntityManager).

## Yêu cầu
- JDK 17+, Maven
- **Tomcat 11** (Jakarta EE 11 / Servlet 6.1)
- SQL Server (TCP/IP port 1433, bật SQL Authentication cho user `sa`)

## Cách chạy
1. Chạy `sql/create-database.sql` để tạo database `ShoppingJPA`.
2. Sửa user / password / databaseName trong `src/main/resources/META-INF/persistence.xml`.
3. (Tuỳ chọn) Chạy `vn.iotstar.config.Test` (main) để Hibernate tạo bảng và thêm dữ liệu thử.
4. `mvn clean package` → deploy `target/ShoppingJPA.war` lên Tomcat (hoặc Run on Server trong IDE).
5. Mở `http://localhost:8080/ShoppingJPA/` → bấm **Đăng ký ngay** tạo tài khoản, rồi đăng nhập.

Ảnh upload lưu ở `<thư mục home>/upload/category`, poster + video lưu ở `<thư mục home>/upload/video` (xem `util/Constant.java`).

### Cấu hình email OTP
Sửa `src/main/resources/mail.properties` (Gmail: dùng *App password* 16 ký tự, không dùng mật khẩu thường) hoặc đặt biến môi
trường `MAIL_USERNAME` / `MAIL_PASSWORD`. Nếu chưa cấu hình, mã OTP được **in ra console của Tomcat** (chế độ DEV) để chạy thử.

## Cấu trúc
```
sql/create-database.sql
src/main/resources/META-INF/persistence.xml     persistence unit "jpa-shopping"
src/main/java/vn/iotstar/
  entity/     Category.java, Video.java, User.java   (@Entity)
  config/     JPAConfig.java (EntityManagerFactory dùng chung), JPAContextListener.java, Test.java
  dao/        ICategoryDao, IUserDao + impl/
  service/    ICategoryService, IUserService + impl/
  controller/ Login, Register, Logout, DownloadImage, admin/{Dashboard,CategoryList,CategoryAdd,CategoryEdit,CategoryDelete}
  filter/     AuthenticationFilter.java (bảo vệ /admin/*)
src/main/webapp/
  WEB-INF/sitemesh3.xml                          cấu hình SiteMesh 3 (chọn decorator theo URL)
  decorators/web.jsp, decorators/admin.jsp       layout Bootstrap 5 (navbar, sidebar, footer)
  views/                                         JSP nội dung (chỉ có <title> + <body>)
  assets/css/app.css, assets/js/app.js           bổ sung nhỏ cho template Bootstrap
```

## SiteMesh 3 + Bootstrap 5 (Bài 1)
- Thư viện `org.sitemesh:sitemesh:3.3.0`. Chạy trên **Tomcat 11**: `sitemesh3.xml` đặt `<dispatch-mode>include</dispatch-mode>`
  và các controller gọi JSP bằng `include()` (Tomcat 11 + `forward()` qua filter SiteMesh cho ra trang trắng).
- `web.xml` khai báo filter `org.sitemesh.config.ConfigurableSiteMeshFilter` cho `/*`.
- `WEB-INF/sitemesh3.xml`: `<decorator-prefix>/decorators/</decorator-prefix>` (mặc định SiteMesh 3.2 là `/WEB-INF/decorators/`); `/*` → `decorators/web.jsp`, `/admin/*` → `decorators/admin.jsp`;
  loại trừ `/assets/*`, `/media`, `/image`.
- Hai decorator dùng chung **một template Bootstrap 5** (CDN jsDelivr + Bootstrap Icons): decorator viết
  `<sitemesh:write property="title"/>`, `<sitemesh:write property="head"/>`, `<sitemesh:write property="body"/>`.
- Các JSP trong `views/` không còn tự viết header/footer/CSS, chỉ còn phần nội dung riêng của trang.

## URL
| URL | Chức năng |
|---|---|
| `/register` | Đăng ký tài khoản (lưu bảng `users`, mật khẩu băm PBKDF2) |
| `/login`, `/logout` | Đăng nhập / đăng xuất |
| `/admin/dashboard` | Tổng quan |
| `/admin/category/list[?page=2]` hoặc `?keyword=...` | Danh sách (phân trang) / tìm kiếm |
| `/admin/category/add` | Thêm (GET form, POST lưu) |
| `/admin/category/edit?id=1` | Sửa (GET form, POST lưu) |
| `/admin/category/delete?id=1` | Xóa |

| `/register` → `/verify-otp` | Đăng ký rồi nhập OTP gửi qua email để **kích hoạt** tài khoản (OTP 6 số, hiệu lực 5 phút, sai tối đa 5 lần) |
| `/forgot-password` → `/reset-password` | Quên mật khẩu: nhập email → nhận OTP → đặt mật khẩu mới |
| `/home` | Trang chủ: 10 video mới nhất; bấm tiêu đề để xem |
| `/watch?id=...` | Chi tiết + phát video (HTML5 `<video>`) |
| `/video[?page=2]` | Video của user đăng nhập, **6 video / trang** |
| `/video/add`, `/video/edit?id=...`, `POST /video/delete` | CRUD Video (upload poster + file video bằng `@MultipartConfig`) |
| `/media?fname=...` | Trả file poster / video (hỗ trợ HTTP Range để tua video) |
| `/profile` | Hồ sơ: sửa họ tên, số điện thoại, email, ảnh đại diện (Cloudinary) |
| `/profile/verify-email` | Nhập OTP gửi tới **email mới** để hoàn tất đổi email (gửi lại / huỷ) |

Quan hệ: `users` 1 — n `videos` (cột `videos.userId`). Mật khẩu băm PBKDF2-HMAC-SHA256 kèm salt ngẫu nhiên.

## Validation các form (Bài 2)
Kiểm tra **2 lớp** cho login, register, quên mật khẩu (nhập email), đặt lại mật khẩu, nhập OTP,
thêm/sửa danh mục, thêm/sửa video:

1. **Client (Bootstrap 5)**: form có `class="needs-validation" novalidate`, dùng `required`, `pattern`,
   `minlength`, `maxlength`, `type=email`; `assets/js/app.js` kiểm tra thêm *nhập lại mật khẩu*
   (`data-match`), *định dạng / dung lượng file* (`data-ext`, `data-max-mb`) và xem trước ảnh.
2. **Server (Bean Validation – Hibernate Validator)**: mỗi form có 1 lớp DTO trong `vn.iotstar.model`
   (`LoginForm`, `RegisterForm`, `ForgotPasswordForm`, `ResetPasswordForm`, `OtpForm`, `CategoryForm`,
   `VideoForm`) gắn `@NotBlank`, `@Size`, `@Pattern`, `@Email`, `@AssertTrue`...
   `ValidationUtil.validate(form)` trả về `Map<field, message>` → controller đặt vào `errors`
   → JSP tô đỏ ô (`is-invalid`) và hiện thông báo ngay dưới ô (`invalid-feedback`).
   File upload được kiểm tra bằng `FileValidator` (đuôi file, MIME, dung lượng).
   Lỗi nghiệp vụ gắn với 1 ô (trùng username/email/tên danh mục) dùng `FieldValidationException`.

| Form | Quy tắc chính |
|---|---|
| Đăng nhập | username, password bắt buộc |
| Đăng ký | họ tên 2-100 ký tự; email đúng định dạng; username `[A-Za-z0-9_]{3,30}`; mật khẩu 6-50 ký tự có chữ + số; nhập lại khớp; username/email chưa dùng |
| Quên / đặt lại mật khẩu | email hợp lệ; OTP đúng 6 chữ số; mật khẩu mới như trên |
| Danh mục | tên 2-100 ký tự, không trùng; trạng thái 0/1; ảnh jpg/png/gif/webp ≤ 2MB |
| Video | tiêu đề 3-255 ký tự; mô tả ≤ 2000; poster ảnh ≤ 5MB; video mp4/webm/ogg/mov ≤ 200MB (bắt buộc khi thêm) |

## Hồ sơ người dùng + OTP email mới + Cloudinary (Bài 3)
- URL `/profile` (phải đăng nhập, bảo vệ bởi `UserAuthFilter`), giao diện do **SiteMesh** decorator `web.jsp` bọc.
- Form `multipart/form-data` (`@MultipartConfig`, ảnh ≤ 5MB) gồm: họ tên, số điện thoại, email, ảnh đại diện.
- **Họ tên, SĐT**: validate bằng `ProfileForm` (SĐT Việt Nam `0xxxxxxxxx` / `+84xxxxxxxxx`, không bắt buộc) rồi lưu ngay.
- **Ảnh đại diện**: `CloudinaryUtil.uploadAvatar(part)` đọc bytes của `Part` và upload lên Cloudinary
  (thư mục `shopping/avatars`, cắt vuông 400×400 theo khuôn mặt). Lưu `secure_url` vào `users.images`,
  `public_id` vào `users.imagePublicId`; ảnh cũ được xoá trên Cloudinary.
- **Email mới**: không đổi ngay. Email mới lưu tạm ở `users.pendingEmail`, OTP 6 số (5 phút, sai tối đa 5 lần)
  được gửi **đến email mới** → `/profile/verify-email` nhập đúng OTP thì `email = pendingEmail`.
  Có nút *Gửi lại mã* (cách nhau ≥ 30 giây) và *Huỷ đổi email*. Email mới phải chưa thuộc tài khoản đã kích hoạt khác.
- Cột mới trong bảng `users`: `phone`, `images`, `imagePublicId`, `pendingEmail` (Hibernate `update` tự thêm,
  hoặc chạy phần ALTER trong `sql/create-database.sql`).

### Cấu hình Cloudinary
Tạo tài khoản miễn phí tại https://cloudinary.com, lấy *Cloud name / API Key / API Secret* trong Dashboard rồi
điền vào `src/main/resources/cloudinary.properties`, **hoặc** đặt biến môi trường
`CLOUDINARY_URL=cloudinary://<api_key>:<api_secret>@<cloud_name>` (nên dùng cách này để không đẩy secret lên GitHub).
Chưa cấu hình thì trang hồ sơ vẫn sửa được họ tên / SĐT / email, chỉ báo chưa đổi được ảnh.
