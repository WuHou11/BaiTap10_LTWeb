package vn.iotstar.Model;
import jakarta.validation.constraints.*;

import jakarta.persistence.*;


@Entity
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 3, max = 50, message = "Họ tên phải từ 3-50 ký tự")
    private String fullname;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu ít nhất 6 ký tự")
    private String password;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải 10-11 chữ số")
    private String phone;

    @NotBlank(message = "Role không được để trống")
    private String role;

}

