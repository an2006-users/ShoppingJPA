package vn.iotstar.dao;

import vn.iotstar.entity.User;

public interface IUserDao {

    void insert(User user);

    /** Trả về null nếu không có. */
    User findByUsername(String username);

    /** Trả về null nếu không có. */
    User findByEmail(String email);
}
