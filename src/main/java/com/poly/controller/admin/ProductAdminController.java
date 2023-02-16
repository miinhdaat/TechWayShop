package com.poly.controller.admin;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.dao.CategoryDao;
import com.poly.dao.ProductDao;
import com.poly.dao.TrademarkDao;
import com.poly.entity.Category;
import com.poly.entity.Product;
import com.poly.entity.Trademark;
import com.poly.service.ProductService;
import com.poly.service.RandomService;
import com.poly.service.UploadService;

@Controller
public class ProductAdminController {
	@Autowired
	ProductDao dao;
	@Autowired
	ProductService proservice;
	@Autowired
	CategoryDao catedao;
	@Autowired
	TrademarkDao tradao;
	@Autowired
	RandomService randomService;
	@Autowired
	UploadService uploadService;

	@GetMapping("/admin/product/list")
	public String index(Model model, HttpServletRequest request, RedirectAttributes redirect) {
		request.getSession().setAttribute("productlist", null);
		if (model.asMap().get("success") != null)
			redirect.addFlashAttribute("success", model.asMap().get("success").toString());
		return "redirect:/admin/product/list/page/1";
	}

	@GetMapping("/admin/product/list/page/{pageNumber}")
	public String showProductPage(HttpServletRequest request, @PathVariable int pageNumber, Model model) {
		List<Product> list = (List<Product>) proservice.findAll();
		model.addAttribute("sizepro", list.size());
		pager(model, request, list, pageNumber);
		getNewPro(model);
		String baseUrl = "/admin/product/list/page/";
		model.addAttribute("baseUrl", baseUrl);
		return "/admin/product/list";
	}

	@GetMapping("/admin/product/findIdorName")
	public String search(@RequestParam("keyword") String keyword, Model model) {
		if (keyword.equals("")) {
			return "redirect:/admin/product/list";
		}
		model.addAttribute("items", dao.finbyIdOrName(keyword));
		model.addAttribute("message", "Tìm kiếm thành công");
		return "/admin/product/list";
	}

	@RequestMapping("/admin/product/findIdorName/{pageNumber}")
	public String findIdorName(Model model, @RequestParam("keyword") String keyword, HttpServletRequest request,
			@PathVariable int pageNumber) {
		if (keyword.equals("")) {
			return "redirect:/admin/product/list";
		}
		List<Product> list = dao.finbyIdOrName(keyword);

		if (list == null) {
			return "redirect:/admin/product/list";
		}
		pagerfind(model, request, list, pageNumber);
		getNewPro(model);
		String baseUrl = "/admin/product/list/page";
		model.addAttribute("baseUrl", baseUrl);
		return "/admin/product/list";
	}

	@RequestMapping("/admin/product/findallkeyword")
	public String findallkeyword(Model model) {
		List<Product> list = dao.findAll();
		model.addAttribute("items", list);
		return "/admin/product/list";
	}

	@RequestMapping("/admin/product/findallkeyword/{pageNumber}")
	public String findallkeyword(Model model, @RequestParam("Category_id") String Category_id,
			@RequestParam("Trademark_id") String Trademark_id, @RequestParam("Status") String Status,
			@RequestParam("MinPrice") Integer unit_price, @RequestParam("MaxPrice") Integer unit_price1,
			HttpServletRequest request, @PathVariable int pageNumber) {
		List<Product> list = dao.findByAllKeyWordAdmin(unit_price, unit_price1, Category_id, Trademark_id, Status);
		model.addAttribute("items.pageList", list);
		pagerfind(model, request, list, pageNumber);
		getNewPro(model);
		String baseUrl = "/admin/product/list/page/";
		model.addAttribute("baseUrl", baseUrl);
		return "/admin/product/list";
	}

	@RequestMapping("/admin/product/edit")
	public String edit(Model model, @RequestParam("product_id") Integer product_id) {
		try {
			Product pro = dao.findById(product_id).get();
			model.addAttribute("pro", pro);
			model.addAttribute("message", "Thao tác thành công");
			return "admin/product/edit";
		} catch (Exception e) {
			model.addAttribute("message", "Thao tác thất bại");
			return "admin/product/edit";
		}
	}

	@RequestMapping("/admin/product/delete/{id}")
	public String delete(Model model, @PathVariable("id") Integer id) {
		try {
			dao.deleteById(id);
			model.addAttribute("message", "Xoá thành công");
			return "redirect:/admin/product/list";
		} catch (Exception e) {
			model.addAttribute("message", "Sản phẩm đang có trong 1 đơn hàng");
			return "redirect:/admin/product/list";
		}

	}

	@PostMapping("/admin/product/create")
	public String create(Product pro, Model model, @RequestParam("photo_img") MultipartFile[] file) throws IOException {
		model.addAttribute("pro", pro);
		Boolean up = uploadImage(file, pro);
		if (up == true) {
			model.addAttribute("message", "Thêm mới thành công");
			return "redirect:/admin/product/edit?product_id=" + pro.getProduct_id();
		} else {
			model.addAttribute("message", "Thêm mới thất bại");
			return "redirect:/admin/product/edit?product_id=" + pro.getProduct_id();
		}
	}

	@RequestMapping("/admin/product/update")
	public String update(Product pro, Model model) throws IOException {
		model.addAttribute("pro", pro);
		if (dao.existsById(pro.getProduct_id())) {
			dao.save(pro);
			model.addAttribute("message", "Cập nhập thành công!");
			return "redirect:/admin/product/edit?product_id=" + pro.getProduct_id();
		} else {
			model.addAttribute("message", "Cập nhập thất bại!");
			return "redirect:/admin/product/edit?product_id=" + pro.getProduct_id();
		}
	}

	@RequestMapping("/admin/product/update/images")
	public String update(@RequestParam("Product_id") Integer id, Model model,
			@RequestParam("photo_img") MultipartFile[] file) throws IOException {
		Product pro = dao.findById(id).get();
		model.addAttribute("pro", pro);
		Boolean up = uploadImage(file, pro);
		if (up == true) {
			model.addAttribute("message", "Cập nhập thành công");
			return "redirect:/admin/product/edit?product_id=" + pro.getProduct_id();
		} else {
			model.addAttribute("message", "Cập nhập thất bại");
			return "redirect:/admin/product/edit?product_id=" + pro.getProduct_id();
		}
	}

	public boolean uploadImage(MultipartFile[] file, Product pro) throws IOException {
		if (pro != null) {
			for (int i = 0; i < file.length; i++) {
				if (i == 0) {
					if (file[i].getOriginalFilename().equals("")) {
						break;
					} else {
						pro.setImage1(up(file[i], pro));
					}
				}
				;
				if (i == 1) {
					pro.setImage2(up(file[i], pro));
				}
				;
				if (i == 2) {
					pro.setImage3(up(file[i], pro));
				}
				;
				if (i == 3) {
					pro.setImage4(up(file[i], pro));
				}
				;
			}
			dao.save(pro);
			return true;
		} else {
			return false;
		}
	}
	
	public String up(MultipartFile file, Product pro) throws IOException {
		String getFileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
				.trim();
		Integer a = pro.getProduct_id();
		if (a==null) {
			String filename = randomService.randomS("new") + "." + getFileType;
			uploadService.save(file, "/assets/images/products", filename);
			return filename;
		} else {
			String filename = randomService.randomS(a.toString()) + "." + getFileType;
			uploadService.save(file, "/assets/images/products", filename);
			return filename;
		}
	}

	public void getNewPro(Model model) {
		Product pro = new Product();
		List<Category> cateList = catedao.findAll();
		List<Trademark> tradeList = tradao.findAll();
		model.addAttribute("cate", cateList);
		model.addAttribute("trade", tradeList);
		model.addAttribute("pro", pro);
	}

	public void pager(Model model, HttpServletRequest request, List<Product> list, int pageNumber) {
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
		int pagesize = 10;
		if (pages == null) {
			pages = new PagedListHolder<>(list);
			pages.setPageSize(pagesize);
		} else {
			final int goToPage = pageNumber - 1;
			if (goToPage <= pages.getPageCount() && goToPage >= 0) {
				pages.setPage(goToPage);
			}
		}
		request.getSession().setAttribute("productlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("items", pages);
		model.addAttribute("sizepro", list.size());
	}

	public void pagerfind(Model model, HttpServletRequest request, List<Product> list, int pageNumber) {
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("productlist");
		int pagesize = 10;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("productlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		request.getSession().setAttribute("productlist", pages);
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("items", pages);
		model.addAttribute("sizepro", list.size());
		model.addAttribute("message", "Tìm kiếm thành công");
	}
}
