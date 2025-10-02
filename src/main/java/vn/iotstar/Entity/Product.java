package vn.iotstar.Entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Products")
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;

	@NotBlank(message = "Tên sản phẩm không được để trống")
	@Size(min = 2, max = 100, message = "Tên sản phẩm phải từ 2 đến 100 ký tự")
	@Column(length = 500, columnDefinition = "nvarchar(500) not null")
	private String productName;

	@NotNull(message = "Số lượng không được để trống")
	@Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
	@Column(nullable = false)
	private Integer quantity;

	@NotNull(message = "Đơn giá không được để trống")
	@Min(value = 0, message = "Đơn giá phải lớn hơn hoặc bằng 0")
	@Column(nullable = false)
	private Double unitPrice;

	@Column(length = 200)
	private String images;

	@NotBlank(message = "Mô tả không được để trống")
	@Size(min = 5, max = 500, message = "Mô tả phải từ 5 đến 500 ký tự")
	@Column(columnDefinition = "nvarchar(500) not null")
	private String description;

	@NotNull(message = "Giảm giá không được để trống")
	@Min(value = 0, message = "Giảm giá phải lớn hơn hoặc bằng 0")
	@Column(nullable = false)
	private Double discount;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createDate;

	@Column(nullable = false)
	private Short status;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "categoryId", nullable = false)
	private Category category;

	@Transient // không lưu DB, chỉ dùng để phân biệt thêm mới / chỉnh sửa
	private Boolean isEdit = false;
}
