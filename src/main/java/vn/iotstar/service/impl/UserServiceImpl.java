package vn.iotstar.service.impl;

import java.util.regex.Pattern;

import vn.iotstar.dao.IUserDao;
import vn.iotstar.dao.impl.UserDao;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;
import vn.iotstar.util.PasswordUtil;

public class UserServiceImpl implements IUserService {

    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,30}$");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final IUserDao userDao = new UserDao();

    @Override
    public void register(String username, String password, String fullname, String email) {
        username = username == null ? "" : username.trim();
        fullname = fullname == null ? "" : fullname.trim();
        email = email == null ? "" : email.trim().toLowerCase();

        if (!USERNAME.matcher(username).matches()) {
            throw new IllegalArgumentException("Tên đăng nhập gồm 3-30 ký tự: chữ không dấu, số hoặc dấu gạch dưới");
        }
        if (fullname.isEmpty() || fullname.length() > 100) {
            throw new IllegalArgumentException("Họ tên không được để trống (tối đa 100 ký tự)");
        }
        if (!EMAIL.matcher(email).matches() || email.length() > 100) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }
        if (userDao.findByUsername(username) != null) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        if (userDao.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        User user = new User();
        user.setUsername(username);
        user.setFullname(fullname);
        user.setEmail(email);
        user.setPassword(PasswordUtil.hash(password));
        userDao.insert(user);
    }

    @Override
    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        User user = userDao.findByUsername(username.trim());
        if (user != null && PasswordUtil.verify(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}
