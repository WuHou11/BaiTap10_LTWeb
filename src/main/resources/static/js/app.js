$(document).ready(function() {
	loadCategories();
	loadProducts();

	// Thêm Category
	$("#addCategoryForm").on("submit", function(e) {
		e.preventDefault();
		let formData = new FormData(this);

		$.ajax({
			url: "/api/category/addCategory",
			type: "POST",
			data: formData,
			processData: false,
			contentType: false,
			success: function(res) {
				if (res.status) {
					alert("Thêm Category thành công!");
					loadCategories();
					$("#addCategoryForm")[0].reset();
				} else {
					alert(res.message || "Có lỗi xảy ra");
				}
			},
			error: function() {
				alert("Lỗi khi thêm Category");
			}
		});
	});

	// Thêm Product
	$("#addProductForm").on("submit", function(e) {
		e.preventDefault();
		let formData = new FormData(this);

		$.ajax({
			url: "/api/product/addProduct",
			type: "POST",
			data: formData,
			processData: false,
			contentType: false,
			success: function(res) {
				if (res.status) {
					alert("Thêm Product thành công!");
					loadProducts();
					$("#addProductForm")[0].reset();
				} else {
					alert(res.message || "Có lỗi xảy ra");
				}
			},
			error: function() {
				alert("Lỗi khi thêm Product");
			}
		});
	});
});

// ===== CATEGORY =====
function loadCategories() {
	$.get("/api/category", function(res) {
		if (res.status) {
			let rows = "";
			let options = "";
			res.body.forEach(c => {
				rows += `<tr>
                  <td>${c.categoryId}</td>
                  <td>${c.categoryName}</td>
                  <td><img src="/uploads/${c.icon || ''}" class="img-thumbnail" style="max-width:50px"></td>
                  <td>
                    <button class="btn btn-sm btn-warning me-1" onclick="showEditCategory(${c.categoryId}, '${c.categoryName}')">Sửa</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteCategory(${c.categoryId})">Xóa</button>
                  </td>
                </tr>`;
				options += `<option value="${c.categoryId}">${c.categoryName}</option>`;
			});
			$("#categoryTable tbody").html(rows);
			$("#categorySelect").html(options);
		}
	});
}

function showEditCategory(id, name) {
	$("#editCategoryId").val(id);
	$("#editCategoryName").val(name);
	$("#editCategoryModal").modal("show");
}

$("#editCategoryForm").on("submit", function(e) {
	e.preventDefault();
	let formData = new FormData(this);

	$.ajax({
		url: "/api/category/updateCategory", // API update category
		type: "PUT",
		data: formData,
		processData: false,
		contentType: false,
		success: function(res) {
			if (res.status) {
				$("#editCategoryModal").modal("hide");
				loadCategories();
			} else {
				alert(res.message || "Cập nhật thất bại");
			}
		}
	});
});


function deleteCategory(id) {
	if (!confirm("Xóa Category này?")) return;
	$.ajax({
		url: "/api/category/deleteCategory?categoryId=" + id,
		type: "DELETE",
		success: function(res) {
			alert(res.message);
			loadCategories();
		},
		error: function() {
			alert("Lỗi khi xóa Category");
		}
	});
}

// ===== PRODUCT =====
function loadProducts() {
	$.get("/api/product", function(res) {
		if (res.status) {
			let rows = "";
			res.body.forEach(p => {
				rows += `<tr>
                  <td>${p.productId}</td>
                  <td>${p.productName}</td>
                  <td>${p.unitPrice}</td>
                  <td>${p.quantity}</td>
                  <td>${p.category ? p.category.categoryName : ""}</td>
                  <td>
                    ${p.image ? `<img src="/uploads/${p.image}" class="img-thumbnail" style="max-width:60px">` : ""}
                  </td>
                  <td>
                    <button class="btn btn-sm btn-warning me-1" onclick="showEditProduct(${p.productId})">Sửa</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteProduct(${p.productId})">Xóa</button>
                  </td>
                </tr>`;
			});
			$("#productTable tbody").html(rows);
		}
	});
}

function showEditProduct(id) {
	// Gọi API lấy chi tiết product
	$.get("/api/product/" + id, function(res) {
		if (res.status) {
			let p = res.body;
			$("#editProductId").val(p.productId);
			$("#editProductName").val(p.productName);
			$("#editProductQuantity").val(p.quantity);
			$("#editProductPrice").val(p.unitPrice);
			$("#editProductDescription").val(p.description);
			$("#editProductDiscount").val(p.discount);
			$("#editProductStatus").val(p.status);

			// Load categories vào select
			$.get("/api/category", function(catRes) {
				if (catRes.status) {
					let options = "";
					catRes.body.forEach(c => {
						options += `<option value="${c.categoryId}" ${p.category && p.category.categoryId == c.categoryId ? "selected" : ""}>${c.categoryName}</option>`;
					});
					$("#editProductCategory").html(options);
				}
			});

			$("#editProductModal").modal("show");
		}
	});
}

$("#editProductForm").on("submit", function(e) {
	e.preventDefault();
	let formData = new FormData(this);

	$.ajax({
		url: "/api/product/updateProduct", // API update product
		type: "PUT",
		data: formData,
		processData: false,
		contentType: false,
		success: function(res) {
			if (res.status) {
				$("#editProductModal").modal("hide");
				loadProducts();
			} else {
				alert(res.message || "Cập nhật thất bại");
			}
		}
	});
});

function deleteProduct(id) {
	if (!confirm("Xóa Product này?")) return;
	$.ajax({
		url: "/api/product/deleteProduct?productId=" + id,
		type: "DELETE",
		success: function(res) {
			alert(res.message);
			loadProducts();
		},
		error: function() {
			alert("Lỗi khi xóa Product");
		}
	});
}
