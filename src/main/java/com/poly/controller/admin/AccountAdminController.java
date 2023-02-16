package com.poly.controller.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.dao.AccountDao;
import com.poly.dao.CommentDao;
import com.poly.dao.OrderDao;
import com.poly.entity.Account;
import com.poly.entity.Order;
import com.poly.service.AccountService;
import com.poly.service.RandomService;
import com.poly.service.UploadService;

@Controller
public class AccountAdminController {
	@Autowired
	AccountService accountService;
	@Autowired
	AccountDao aDao;
	@Autowired
	OrderDao odao;
	@Autowired
	CommentDao commentDao;
	@Autowired
	PasswordEncoder pe;
	@Autowired
	RandomService randomService;
	@Autowired
	UploadService uploadService;

	@GetMapping("admin/account/list")
	public String index(Model model, HttpServletRequest request, RedirectAttributes redirect) {
		request.getSession().setAttribute("accountlist", null);
		if (model.asMap().get("success") != null)
			redirect.addFlashAttribute("success", model.asMap().get("success").toString());
		return "redirect:/admin/account/list/page/1";
	}

	@GetMapping("/admin/account/list/page/{pageNumber}")
	public String showProductPage(HttpServletRequest request, @PathVariable int pageNumber, Model model) {
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("accountlist");
		int pagesize = 9;
		List<Account> list = (List<Account>) accountService.findAll();
		model.addAttribute("sizepro", list.size());
		if (pages == null) {
			pages = new PagedListHolder<>(list);
			pages.setPageSize(pagesize);
		} else {
			final int goToPage = pageNumber - 1;
			if (goToPage <= pages.getPageCount() && goToPage >= 0) {
				pages.setPage(goToPage);
			}
		}
		request.getSession().setAttribute("accountlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		newAcc(model);
		String baseUrl = "/admin/account/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/account/list";
	}

	@GetMapping("/admin/account/findIdorName")
	public String search(@RequestParam("keyword") String keyword, Model model) {
		if (keyword.equals("")) {
			return "redirect:/admin/account/list";
		}
		newAcc(model);
		model.addAttribute("items", aDao.finbyIdOrName(keyword));
		return "/admin/account/list";
	}

	@RequestMapping("/admin/account/findIdorName/{pageNumber}")
	public String findIdorName(Model model, @RequestParam("keyword") String keyword, HttpServletRequest request,
			@PathVariable int pageNumber) {
		if (keyword.equals("")) {
			return "redirect:/admin/account/list";
		}
		List<Account> list = aDao.finbyIdOrName(keyword);
		if (list == null) {
			return "redirect:/admin/account/list";
		}
		model.addAttribute("sizepro", list.size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("accountlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("accountlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		newAcc(model);
		String baseUrl = "/admin/account/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/account/list";
	}

	@RequestMapping("admin/account/edit")
	public String edit(Model model, @RequestParam("username") String username) {
		try {
			Account acc = aDao.findById(username).get();

			model.addAttribute("acc", acc);
			List<Order> order = odao.findByUsername(username);
			model.addAttribute("order", order);
			model.addAttribute("listOrder", order.size());
			model.addAttribute("message", "Hiện chi tiết khách hàng thành công");
			return "admin/account/edit";
		} catch (Exception e) {
			model.addAttribute("message", "Hiện chi tiết khách hàng thất bại");
			return "admin/account/edit";
		}
	}

	@RequestMapping("admin/account/update/images")
	public String updateI(@RequestParam("Username") String Username, Model model,
			@RequestParam("photo_img") MultipartFile file) throws IOException {
		Account acc = accountService.findById(Username);
		if (acc != null) {
			String getFileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
					.trim();
			String filename = randomService.randomS(Username) + "." + getFileType;
			uploadService.save(file, "/assets/images/client", filename);
			if (filename == "") {
				model.addAttribute("message", "Upload ảnh thất bại!");
				return "redirect:/admin/account/edit?username=" + acc.getUsername();
			} else {
				acc.setPhoto(filename);
				accountService.update(acc);
				model.addAttribute("message", "Upload ảnh thành công");
				return "redirect:/admin/account/edit?username=" + acc.getUsername();
			}
		} else {
			model.addAttribute("message", "Cập nhập thất bại");
			return "redirect:/admin/account/edit?username=" + acc.getUsername();
		}
	}

	@RequestMapping("admin/account/update")
	public String update(Account acc, Model model) throws IOException {
		if (aDao.existsById(acc.getUsername())) {
			accountService.update(acc);
			model.addAttribute("message", "Cập nhập thành công");
			return "redirect:/admin/account/edit?username=" + acc.getUsername();
		} else {
			model.addAttribute("message", "Cập nhập thất bại");
			return "redirect:/admin/account/edit?username=" + acc.getUsername();
		}
	}

	@RequestMapping("/admin/account/delete/{username}")
	public String delete(Model model, @PathVariable("username") String username) {
		try {
			aDao.deleteById(username);
			model.addAttribute("message", "Xoá thành công");
			return "redirect:/admin/account/list";
		} catch (Exception e) {
			model.addAttribute("message", "Xóa thất bại ");
			return "redirect:/admin/account/list";
		}
	}

	@RequestMapping("/admin/account/create")
	public String create(Account acc, Model model, @RequestParam("photo_img") MultipartFile file) throws IOException {
		if (!aDao.existsById(acc.getUsername())) {
			Account a = aDao.findByEmail(acc.getEmail());
			if (a == null) {
				String getFileType = file.getOriginalFilename()
						.substring(file.getOriginalFilename().lastIndexOf(".") + 1).trim();
				String filename = randomService.randomS(acc.getUsername()) + "." + getFileType;
				uploadService.save(file, "/assets/images/client", filename);
				String pw = acc.getPassword();
				acc.setPassword(pe.encode(pw));
				if (file.isEmpty()) {
					aDao.save(acc);
					model.addAttribute("message", "Thêm mới thành công");
					return "redirect:/admin/account/edit?username=" + acc.getUsername();
				} else {
					acc.setPhoto(filename);
					aDao.save(acc);
					model.addAttribute("message", "Thêm mới thành công");
					return "redirect:/admin/account/edit?username=" + acc.getUsername();
				}
			} else {
				model.addAttribute("message", "Lỗi! Email đã tồn tại!");
				return "redirect:/admin/account/list/page/1";
			}
		} else {
			model.addAttribute("message", "Lỗi! Username đã tồn tại!");
			return "redirect:/admin/account/list/page/1";
		}
	}

	public void newAcc(Model model) {
		Account acc = new Account();
		model.addAttribute("acc", acc);
	}
}
