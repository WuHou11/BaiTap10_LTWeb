package vn.iotstar.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import vn.iotstar.Entity.Category;
import vn.iotstar.Service.ICategoryService;

@Controller
@RequestMapping("admin/categories")
public class CategoryController {

	@Autowired
	ICategoryService categoryService;

	@GetMapping("add")
	public String add(ModelMap model) {
		Category category = new Category();
		category.setIsEdit(false);
		model.addAttribute("category", category);
		return "admin/categories/addOrEdit";
	}

	@GetMapping("list")
	public String list(ModelMap model) {
		model.addAttribute("categories", categoryService.findAll());
		return "admin/categories/list";
	}

	@PostMapping("saveOrUpdate")
	public String saveOrUpdate(ModelMap model, @Valid @ModelAttribute("category") Category category,
			BindingResult result) {

		if (result.hasErrors()) {
			return "admin/categories/addOrEdit";
		}

		categoryService.save(category);

		String message = category.getIsEdit() ? "Category is edited!" : "Category is saved!";
		model.addAttribute("message", message);

		return "redirect:/admin/categories/list";
	}

	@GetMapping("edit/{categoryId}")
	public String edit(ModelMap model, @PathVariable("categoryId") Long categoryId) {
		Optional<Category> optCategory = categoryService.findById(categoryId);

		if (optCategory.isPresent()) {
			Category category = optCategory.get();
			category.setIsEdit(true);

			model.addAttribute("category", category);
			return "admin/categories/addOrEdit";
		}

		model.addAttribute("message", "Category does not exist!");
		return "forward:/admin/categories/list";
	}

	@GetMapping("delete/{categoryId}")
	public String delete(ModelMap model, @PathVariable("categoryId") Long categoryId) {
		categoryService.deleteById(categoryId);
		model.addAttribute("message", "Category is deleted!");
		return "forward:/admin/categories/list";
	}

	@GetMapping("search")
	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
		List<Category> list;
		if (StringUtils.hasText(name)) {
			list = categoryService.findByCategoryNameContaining(name);
		} else {
			list = categoryService.findAll();
		}
		model.addAttribute("categories", list);
		return "admin/categories/search";
	}

	@RequestMapping("searchpaging")
	public String searchPaginated(ModelMap model,
			@RequestParam(name = "name", required = false, defaultValue = "") String name,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);

		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("categoryName"));
		Page<Category> resultPage;

		if (StringUtils.hasText(name)) {
			resultPage = categoryService.findByCategoryNameContaining(name, pageable);
		} else {
			resultPage = categoryService.findAll(pageable);
		}

		int totalPages = resultPage.getTotalPages();
		if (totalPages > 0) {
			int start = Math.max(0, currentPage - 2);
			int end = Math.min(currentPage + 2, totalPages - 1);
			List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		model.addAttribute("categoryPage", resultPage);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("name", name);

		return "admin/categories/searchpaging";
	}
}
