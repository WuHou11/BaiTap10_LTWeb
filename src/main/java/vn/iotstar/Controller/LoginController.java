
package vn.iotstar.Controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import vn.iotstar.Entity.User;
import vn.iotstar.Model.LoginForm;
import vn.iotstar.Service.UserService;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login"; 
    }

    @PostMapping("/login")
    public String doLogin(@Valid @ModelAttribute("loginForm") LoginForm form,
                          BindingResult result,
                          HttpSession session,
                          Model model) {
        if (result.hasErrors()) {
            return "login";
        }

        User user = userService.login(form.getUsername(), form.getPassword());
        if (user == null) {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
            return "login";
        }

        session.setAttribute("user", user);
        session.setAttribute("role", user.getRole());

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/admin";
        } else if ("USER".equalsIgnoreCase(user.getRole())) {
            return "redirect:/user";
        } else {
            session.invalidate();
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
