package vn.iotstar.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.Entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

//    List<CategoryEntity> findByNameContaining(String name);
//
//    Page<CategoryEntity> findByNameContaining(String name, Pageable pageable);

	// Tìm Kiếm theo nội dung tên
	List<Category> findByCategoryNameContaining(String name);

	// Tìm kiếm và Phân trang
	Page<Category> findByCategoryNameContaining(String name, Pageable pageable);

	Optional<Category> findByCategoryName(String name);
}