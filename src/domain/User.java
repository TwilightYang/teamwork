package domain;

/**
 * 用户类  用户信息：用户名和密码
 */
public class User {
    private String username;
    private String password;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 获取用户名
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     * @return password
     */

    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 将User对象转换成字符串
     */
    public String toString() {
        return "username=" + username + "&password=" + password;
    }
}
