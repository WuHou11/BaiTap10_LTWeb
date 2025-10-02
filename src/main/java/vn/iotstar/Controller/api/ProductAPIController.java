package vn.iotstar.Controller.api;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.iotstar.Entity.Category;
import vn.iotstar.Entity.Product;
import vn.iotstar.Model.Response;
import vn.iotstar.Service.ICategoryService;
import vn.iotstar.Service.IProductService;
import vn.iotstar.Service.IStorageService;

@RestController
@RequestMapping("/api/product")
public class ProductAPIController {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return new ResponseEntity<>(
                new Response(true, "Danh sách sản phẩm", productService.findAll()), 
                HttpStatus.OK
        );
    }

    @PostMapping("/getProduct")
    public ResponseEntity<?> getProduct(@RequestParam("id") Long id) {
        Optional<Product> opt = productService.findById(id);
        return opt.map(p -> new ResponseEntity<>(new Response(true, "Thành công", p), HttpStatus.OK))
                  .orElse(new ResponseEntity<>(new Response(false, "Không tìm thấy sản phẩm", null), HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam("keyword") String keyword) {
        return new ResponseEntity<>(
                new Response(true, "Kết quả tìm kiếm", productService.findByProductNameContaining(keyword)),
                HttpStatus.OK
        );
    }

    @GetMapping("/searchPage")
    public ResponseEntity<?> searchProductsPage(@RequestParam("keyword") String keyword, Pageable pageable) {
        Page<Product> page = productService.findByProductNameContaining(keyword, pageable);
        return new ResponseEntity<>(new Response(true, "Kết quả tìm kiếm có phân trang", page), HttpStatus.OK);
    }

    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(
            @Valid @ModelAttribute Product product,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("categoryId") Long categoryId) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(
                    new Response(false, bindingResult.getAllErrors().get(0).getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<Category> categoryOpt = categoryService.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Category không tồn tại", null), HttpStatus.BAD_REQUEST);
        }

        product.setCategory(categoryOpt.get());
        product.setCreateDate(new Date());

        if (image != null && !image.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String fileName = storageService.getSorageFilename(image, uuid.toString());
            product.setImages(fileName);
            storageService.store(image, fileName);
        }

        productService.save(product);
        return new ResponseEntity<>(new Response(true, "Thêm sản phẩm thành công", product), HttpStatus.OK);
    }

    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute Product productUpdate,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("categoryId") Long categoryId) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(
                    new Response(false, bindingResult.getAllErrors().get(0).getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<Product> optProduct = productService.findById(id);
        if (optProduct.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy sản phẩm", null), HttpStatus.BAD_REQUEST);
        }

        Optional<Category> categoryOpt = categoryService.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Category không tồn tại", null), HttpStatus.BAD_REQUEST);
        }

        Product product = optProduct.get();
        product.setProductName(productUpdate.getProductName());
        product.setQuantity(productUpdate.getQuantity());
        product.setUnitPrice(productUpdate.getUnitPrice());
        product.setDescription(productUpdate.getDescription());
        product.setDiscount(productUpdate.getDiscount());
        product.setStatus(productUpdate.getStatus());
        product.setCategory(categoryOpt.get());

        if (image != null && !image.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String fileName = storageService.getSorageFilename(image, uuid.toString());
            product.setImages(fileName);
            storageService.store(image, fileName);
        }

        productService.save(product);
        return new ResponseEntity<>(new Response(true, "Cập nhật sản phẩm thành công", product), HttpStatus.OK);
    }

    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        Optional<Product> opt = productService.findById(id);
        if (opt.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy sản phẩm", null), HttpStatus.BAD_REQUEST);
        }

        productService.delete(opt.get());
        return new ResponseEntity<>(new Response(true, "Xóa sản phẩm thành công", opt.get()), HttpStatus.OK);
    }
}
