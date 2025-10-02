package vn.iotstar.Controller.api;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.iotstar.Entity.Category;
import vn.iotstar.Model.CategoryModel;
import vn.iotstar.Service.ICategoryService;
import vn.iotstar.Service.IStorageService;
import vn.iotstar.Model.Response;

@RestController
@RequestMapping(path = "/api/category")
public class CategoryAPIController {
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IStorageService storageService;

    @GetMapping
    public ResponseEntity<?> getAllCategory() {
        return new ResponseEntity<Response>(
                new Response(true, "Thành công", categoryService.findAll()),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable("id") Long id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(new Response(true, "Thành công", category.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Response(false, "Không tìm thấy Category", null));
    }

    @PostMapping(path = "/add")
    public ResponseEntity<?> addCategory(
            @Valid @ModelAttribute CategoryModel categoryModel,
            BindingResult result,
            @RequestParam("icon") MultipartFile icon) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        Optional<Category> optCategory = categoryService.findByCategoryName(categoryModel.getName());
        if (optCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Category đã tồn tại trong hệ thống");
        }

        Category category = new Category();
        // upload icon
        if (!icon.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String uuString = uuid.toString();
            category.setIcon(storageService.getSorageFilename(icon, uuString));
            storageService.store(icon, category.getIcon());
        }

        category.setCategoryName(categoryModel.getName());
        category.setDescription(categoryModel.getDescription());

        categoryService.save(category);
        return ResponseEntity.ok(new Response(true, "Thêm Thành công", category));
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute CategoryModel categoryModel,
            BindingResult result,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        Optional<Category> optCategory = categoryService.findById(id);
        if (optCategory.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Không tìm thấy Category", null));
        }

        Category category = optCategory.get();

        if (icon != null && !icon.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String uuString = uuid.toString();
            category.setIcon(storageService.getSorageFilename(icon, uuString));
            storageService.store(icon, category.getIcon());
        }

        category.setCategoryName(categoryModel.getName());
        category.setDescription(categoryModel.getDescription());

        categoryService.save(category);
        return ResponseEntity.ok(new Response(true, "Cập nhật Thành công", category));
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) {
        Optional<Category> optCategory = categoryService.findById(id);
        if (optCategory.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Không tìm thấy Category", null));
        }

        categoryService.delete(optCategory.get());
        return ResponseEntity.ok(new Response(true, "Xóa Thành công", optCategory.get()));
    }
}
