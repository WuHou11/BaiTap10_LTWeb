package vn.iotstar.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.iotstar.Entity.Product;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping({ "", "/", "/home" })
    public String home() {
        return "admin/admin_home";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("product", new Product());
        return "admin/products/list"; 
    }
}