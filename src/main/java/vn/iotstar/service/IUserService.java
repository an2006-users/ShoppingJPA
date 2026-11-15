package vn.iotstar.service;

import vn.iotstar.entity.User;

public interface IUserService {

    /**
     * Đăng ký tài khoản mới (mật khẩu truyền vào là mật khẩu gốc, service sẽ băm).
     *
     * @throws IllegalArgumentException nếu dữ liệu sai hoặc username / email đã tồn tại
     */
    void register(String username, String password, String fullname, String email);

    /** Trả về User nếu đúng tài khoản và mật khẩu, ngược lại trả về null. */
    User login(String username, String password);
}
