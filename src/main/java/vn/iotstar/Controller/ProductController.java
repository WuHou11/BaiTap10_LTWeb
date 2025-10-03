package vn.iotstar.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import vn.iotstar.Entity.Product;
import vn.iotstar.Service.IProductService;
import vn.iotstar.Service.ICategoryService;

@Controller
@RequestMapping("admin/products")
public class ProductController {

    @Autowired
    IProductService productService;

    @Autowired
    ICategoryService categoryService; // để load danh mục khi thêm/sửa sản phẩm

    // Hiển thị form thêm mới
    @GetMapping("add")
    public String add(ModelMap model) {
        Product product = new Product();
        product.setIsEdit(false);
        product.setStatus((short) 1); // mặc định status = 1 (active)

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());

        return "admin/products/addOrEdit";
    }

    // Hiển thị danh sách
    @GetMapping("list")
    public String list(ModelMap model) {
        model.addAttribute("products", productService.findAll());
        return "admin/products/list";
    }

    // Lưu hoặc cập nhật
    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(ModelMap model, 
                               @Valid @ModelAttribute("product") Product product,
                               BindingResult result) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "admin/products/addOrEdit";
        }

        productService.save(product);

        String message = product.getIsEdit() ? "Product is updated!" : "Product is saved!";
        model.addAttribute("message", message);

        return "redirect:/admin/products/list";
    }

    // Sửa
    @GetMapping("edit/{productId}")
    public String edit(ModelMap model, @PathVariable("productId") Long productId) {
        Optional<Product> optProduct = productService.findById(productId);

        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            product.setIsEdit(true);

            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.findAll());

            return "admin/products/addOrEdit";
        }

        model.addAttribute("message", "Product does not exist!");
        return "forward:/admin/products/list";
    }

    // Xóa
    @GetMapping("delete/{productId}")
    public String delete(ModelMap model, @PathVariable("productId") Long productId) {
        productService.deleteById(productId);
        model.addAttribute("message", "Product is deleted!");
        return "forward:/admin/products/list";
    }

    // Tìm kiếm
    @GetMapping("search")
    public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
        List<Product> list;
        if (StringUtils.hasText(name)) {
            list = productService.findByProductNameContaining(name);
        } else {
            list = productService.findAll();
        }
        model.addAttribute("products", list);
        return "admin/products/search";
    }

    // Tìm kiếm + phân trang
    @RequestMapping("searchpaging")
    public String searchPaginated(ModelMap model,
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);

        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("productName"));
        Page<Product> resultPage;

        if (StringUtils.hasText(name)) {
            resultPage = productService.findByProductNameContaining(name, pageable);
        } else {
            resultPage = productService.findAll(pageable);
        }

        int totalPages = resultPage.getTotalPages();
        if (totalPages > 0) {
            int start = Math.max(0, currentPage - 2);
            int end = Math.min(currentPage + 2, totalPages - 1);
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("productPage", resultPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("name", name);

        return "admin/products/searchpaging";
    }
}
