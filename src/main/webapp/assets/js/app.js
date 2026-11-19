// Script dùng chung cho template Bootstrap (nạp ở cuối 2 decorator)
document.addEventListener("DOMContentLoaded", function () {
    // Năm / ngày hiện tại ở footer và navbar
    const now = new Date();
    document.querySelectorAll("[data-year]").forEach(el => el.textContent = now.getFullYear());
    document.querySelectorAll("[data-today]").forEach(el => el.textContent = now.toLocaleDateString("vi-VN", {
        weekday: "long", day: "2-digit", month: "2-digit", year: "numeric"
    }));

    // Đánh dấu menu đang mở: so khớp data-nav với đường dẫn (bỏ context path)
    const path = window.location.pathname;
    let best = null;
    document.querySelectorAll("[data-nav]").forEach(link => {
        const key = link.getAttribute("data-nav");
        const idx = path.indexOf(key);
        if (idx >= 0 && (!best || key.length > best.key.length)) {
            best = { link: link, key: key };
        }
    });
    if (best) {
        best.link.classList.add("active");
        best.link.setAttribute("aria-current", "page");
    }

    initFormValidation();
});

/*
 * Validation phía client theo chuẩn Bootstrap 5:
 *  - form có class "needs-validation" + thuộc tính novalidate
 *  - dùng ràng buộc HTML5 (required, pattern, minlength, maxlength, type=email)
 *  - bổ sung: data-match="#id" (nhập lại mật khẩu), data-ext / data-max-mb (file upload)
 * Server vẫn kiểm tra lại toàn bộ bằng Bean Validation (không tin dữ liệu từ trình duyệt).
 */
function initFormValidation() {
    const feedbackOf = input => {
        const group = input.closest(".mb-3, .mb-2, .mb-4") || input.parentElement;
        return group ? group.querySelector(".invalid-feedback") : null;
    };

    const setCustom = (input, message) => {
        input.setCustomValidity(message);
        const fb = feedbackOf(input);
        if (fb && message) fb.textContent = message;
    };

    const checkMatch = input => {
        const other = document.querySelector(input.dataset.match);
        const msg = other && input.value && input.value !== other.value
            ? (input.dataset.matchMsg || "Giá trị nhập lại không khớp") : "";
        setCustom(input, msg);
    };

    const checkFile = input => {
        const file = input.files && input.files[0];
        let msg = "";
        if (file) {
            const exts = (input.dataset.ext || "").split(",").filter(Boolean);
            const ext = file.name.includes(".") ? file.name.split(".").pop().toLowerCase() : "";
            const maxMb = parseFloat(input.dataset.maxMb || "0");
            if (exts.length && !exts.includes(ext)) {
                msg = "Chỉ chấp nhận định dạng: " + exts.join(", ");
            } else if (maxMb && file.size > maxMb * 1024 * 1024) {
                msg = "File tối đa " + maxMb + "MB (file của bạn " + (file.size / 1024 / 1024).toFixed(1) + "MB)";
            }
        }
        setCustom(input, msg);
        // Xem trước ảnh
        const preview = input.dataset.preview && document.querySelector(input.dataset.preview);
        if (preview) {
            const hide = input.dataset.previewHide && document.querySelector(input.dataset.previewHide);
            if (file && !msg && file.type.startsWith("image/")) {
                if (!preview.dataset.original) preview.dataset.original = preview.getAttribute("src") || "";
                preview.src = URL.createObjectURL(file);
                preview.classList.remove("d-none");
                if (hide) hide.classList.add("d-none");
            } else if (preview.dataset.original) {
                preview.src = preview.dataset.original; // quay lại ảnh hiện tại
            } else {
                preview.classList.add("d-none");
                if (hide) hide.classList.remove("d-none");
            }
        }
    };

    document.querySelectorAll("form.needs-validation").forEach(form => {
        // Lỗi do server trả về: bỏ đỏ khi người dùng sửa lại ô đó
        form.querySelectorAll(".is-invalid").forEach(input => {
            const clear = () => input.classList.remove("is-invalid");
            input.addEventListener("input", clear, { once: true });
            input.addEventListener("change", clear, { once: true });
        });

        form.querySelectorAll("[data-match]").forEach(input => {
            const other = document.querySelector(input.dataset.match);
            input.addEventListener("input", () => checkMatch(input));
            if (other) other.addEventListener("input", () => checkMatch(input));
        });

        form.querySelectorAll("input[type=file][data-ext], input[type=file][data-max-mb]").forEach(input => {
            input.addEventListener("change", () => checkFile(input));
        });

        form.addEventListener("submit", event => {
            form.querySelectorAll("[data-match]").forEach(checkMatch);
            form.querySelectorAll("input[type=file][data-ext], input[type=file][data-max-mb]").forEach(checkFile);
            form.querySelectorAll(".is-invalid").forEach(el => el.classList.remove("is-invalid"));

            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
                const firstInvalid = form.querySelector(":invalid");
                if (firstInvalid) firstInvalid.focus();
            }
            form.classList.add("was-validated");
        });
    });
}
