package com.poly.controller.admin;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.dao.AccountDao;
import com.poly.dao.OrderDao;
import com.poly.dao.OrderDetailDao;
import com.poly.dao.ProductDao;
import com.poly.entity.Account;
import com.poly.entity.Order;
import com.poly.entity.OrderDetail;
import com.poly.entity.Product;
import com.poly.service.SendMailService;

@Controller
public class OrderAdminController {
	@Autowired
	OrderDao odao;
	@Autowired
	OrderDetailDao otddao;
	@Autowired
	ProductDao prodao;
	@Autowired
	AccountDao adao;
	@Autowired
	SendMailService service;

	@GetMapping("/admin/order/list")
	public String index(Model model, HttpServletRequest request, RedirectAttributes redirect) {
		request.getSession().setAttribute("orderlist", null);
		if (model.asMap().get("success") != null)
			redirect.addFlashAttribute("success", model.asMap().get("success").toString());
		return "redirect:/admin/order/list/page/1";
	}

	@GetMapping("/admin/order/list/page/{pageNumber}")
	public String showProductPage(HttpServletRequest request, @PathVariable int pageNumber, Model model) {
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("orderlist");
		int pagesize = 9;
		List<Order> list = (List<Order>) odao.findByAllDesc();
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
		request.getSession().setAttribute("orderlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/admin/order/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/order/list";
	}

	@GetMapping("/admin/order/findbyOrderId")
	public String search(@RequestParam("order_id") String order_id, Model model) {
		if (order_id.equals("")) {
			return "redirect:/admin/order/list";
		}
		model.addAttribute("items", odao.findByOrder_Id(order_id));
		return "/admin/order/list";
	}

	@RequestMapping("/admin/order/findbyOrderId/{pageNumber}")
	public String findIdorName(Model model, @RequestParam("order_id") String order_id, HttpServletRequest request,
			@PathVariable int pageNumber) {
		if (order_id.equals("")) {
			return "redirect:/admin/order/list";
		}
		List<Order> list = odao.findByOrder_Id(order_id);
		if (list == null) {
			return "redirect:/admin/order/list";
		}
		model.addAttribute("sizepro", list.size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("orderlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("orderlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/admin/order/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/order/list";
	}

	@RequestMapping("/admin/order/findallkeyword")
	public String findallkeyword(Model model) {
		List<Order> list = odao.findAll();
		model.addAttribute("items", list);
		return "/admin/order/list";
	}

	@SuppressWarnings("deprecation")
	@RequestMapping("/admin/order/findByAllkeyword/{pageNumber}")
	public String findByAllkeyword(Model model, @RequestParam("Username") String username,
			@RequestParam(value = "Minday", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date minday,
			@RequestParam(value = "Maxday", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date maxday,
			@RequestParam("Status") String Status, @RequestParam("Phone") String Phone,
			@RequestParam("MinPrice") Integer unit_price, @RequestParam("MaxPrice") Integer unit_price1,
			HttpServletRequest request, @PathVariable int pageNumber) {
		if (minday == (null)) {
			minday = new Date(2001 - 01 - 23);
		}
		if (maxday == (null)) {
			maxday = new Date();
		}

		List<Order> list = odao.findByAllKeyWord(username, minday, maxday, Status, Phone, unit_price, unit_price1);
		model.addAttribute("orders", list);
		model.addAttribute("sizepro", list.size());
		PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("orderlist");
		int pagesize = 9;
		pages = new PagedListHolder<>(list);
		pages.setPageSize(pagesize);
		final int goToPage = pageNumber - 1;
		if (goToPage <= pages.getPageCount() && goToPage >= 0) {
			pages.setPage(goToPage);
		}
		request.getSession().setAttribute("orderlist", pages);
		int current = pages.getPage() + 1;
		int begin = Math.max(1, current - list.size());
		int end = Math.min(begin + 5, pages.getPageCount());
		int totalPageCount = pages.getPageCount();
		String baseUrl = "/admin/order/list/page/";
		model.addAttribute("beginIndex", begin);
		model.addAttribute("endIndex", end);
		model.addAttribute("currentIndex", current);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("items", pages);
		return "/admin/order/list";

	}

	@RequestMapping("/admin/order/edit")
	public String orderDetail(Model model, @RequestParam("order_id") Integer order_id) {

		Order ListOrder = odao.findById(order_id).get();
		List<OrderDetail> ListOrderDetail = otddao.findByOrderID(order_id);
		model.addAttribute("ord", ListOrder);
		model.addAttribute("odetail", ListOrderDetail);
		model.addAttribute("message", "Thao tác thành công");
		return "admin/order/edit";

	}

	@RequestMapping("/admin/order/delete/{order_id}")
	public String deleteOrder_Id(Model model, @PathVariable("order_id") Integer order_id) {
		otddao.deleteOrderId(order_id);
		odao.deleteById(order_id);
		return "admin/order/list";
	}

	@RequestMapping("/admin/order/delete/form/{order_id}")
	public String deleteformOrder_Id(Model model, @PathVariable("order_id") Integer order_id) {
		otddao.deleteOrderId(order_id);
		odao.deleteById(order_id);
		return "admin/order/list";
	}

	@RequestMapping("/admin/order/updateStatus")
	public String updateStatus(Model model, @RequestParam("Order_id") Integer Order_id,
			@RequestParam("Status") Integer Status) {
		System.out.println("ORDER ID: " + Order_id);
		System.out.println("STATUS: " + Status);
		Order ord = odao.findById(Order_id).get();
		if (odao.existsById(Order_id)) {
			if (Status == 1) {
				List<OrderDetail> listOrder = otddao.findByOrderID(Order_id);
				for (int i = 0; i < listOrder.size(); i++) {
					Optional<Product> pro = prodao.findById(listOrder.get(i).getProduct().getProduct_id());
					pro.orElseThrow().setQuantity(pro.get().getQuantity() - listOrder.get(i).getQuantity());
					prodao.save(pro.get());
				}
			}
			if (Status == 2) {
				List<OrderDetail> listOrder = otddao.findByOrderID(Order_id);
				for (int i = 0; i < listOrder.size(); i++) {
					Optional<Product> pro = prodao.findById(listOrder.get(i).getProduct().getProduct_id());
					pro.orElseThrow().setQuantity(pro.get().getQuantity() + listOrder.get(i).getQuantity());
					prodao.save(pro.get());
				}
			}
			if (Status == 3) {
				ord.setDescription("Đã thanh toán");
			}
			model.addAttribute("message", "Thao tác thành công");
			ord.setStatus(Status);
			odao.save(ord);
			return "redirect:/admin/order/edit?order_id=" + ord.getOrder_id();
		} else {
			model.addAttribute("message", "Thao tác thất bại");
			return "redirect:/admin/order/edit?order_id=" + ord.getOrder_id();
		}
	}

	public void sendMailAction(Order oReal, String subject) {
		List<OrderDetail> list = otddao.findByOrderID(oReal.getOrder_id());
		Account acc = adao.findById(oReal.getAccount().getUsername()).get();
		StringBuilder stringBuilder = new StringBuilder();
		String status = "";
		Integer st = oReal.getStatus();
		if (st == 1) {
			status = "Đang giao hàng!";
		} else if (st == 3) {
			status = "Đã hoàn thành!";
		} else if (st == 2) {
			status = "Huỷ giao hàng!";
		} else if (st == 0) {
			status = "Đang xử lý!";
		} else {
			status = "Đã huỷ!";
		}
		int index = 0;
		stringBuilder.append("<p>Xin chào " + acc.getFullname() + ",</p>\r\n"
				+ "<p>Cửa hàng xin thông báo đơn đặt hàng mang mã số #" + oReal.getOrder_id() + ".</p>\r\n"
				+ "<p>Đơn hàng của bạn đã được cập nhật trạng thái. Dưới đây là thông tin đơn hàng.</p>\r\n"
				+ "<p>Trạng thái đơn hàng: <span style=\"color: red\">" + status + "</span>.</p>\r\n"
				+ "<p>http://localhost:8888/order/detail/" + oReal.getOrder_id() + "</p>\r\n" + "<hr>\r\n"
				+ "<h4>CHI TIẾT ĐƠN HÀNG</h4>" + "    <table style=\"border: 1px solid gray;\">\r\n"
				+ "        <tr style=\"width: 100%; border: 1px solid gray;\">\r\n"
				+ "            <th style=\"border: 1px solid gray;\">STT</th>\r\n"
				+ "            <th style=\"border: 1px solid gray;\">Tên sản phẩm</th>\r\n"
				+ "            <th style=\"border: 1px solid gray;\">Số lượng</th>\r\n"
				+ "            <th style=\"border: 1px solid gray;\">Đơn giá</th>\r\n" + "        </tr>");
		for (OrderDetail oD : list) {
			index++;
			stringBuilder.append("<tr>\r\n" + "            <td style=\"border: 1px solid gray;\">" + index + "</td>\r\n"
					+ "            <td style=\"border: 1px solid gray;\">" + nameP(oD.getProduct().getProduct_id())
					+ "</td>\r\n" + "            <td style=\"border: 1px solid gray;\">" + oD.getQuantity()
					+ "</td>\r\n" + "            <td style=\"border: 1px solid gray;\">"
					+ format(String.valueOf(oD.getPrice())) + "</td>\r\n" + "        </tr>");
		}
		stringBuilder.append("\r\n" + "    </table>\r\n" + "    <h3>Tổng tiền: "
				+ format(String.valueOf(oReal.getPrice())) + "</h3>\r\n" + "<hr>\r\n" + "<h4>THÔNG TIN GIAO NHẬN</h4>"
				+ "<p>Họ và tên: " + acc.getFullname() + "</p>\r\n" + "<p>Điện thoại: " + oReal.getPhone() + "</p>\r\n"
				+ "<p>Email: " + acc.getEmail() + "</p>\r\n" + "<p>Địa chỉ nhận hàng: " + oReal.getAddress()
				+ "</p>\r\n" + "<p>Phương thức thanh toán: " + oReal.getMethod() + "</p>\r\n" + "<hr>\r\n"
				+ "<h5>Chúc bạn một ngày tốt lành!</h5>");
		service.queue(acc.getEmail(), subject, stringBuilder.toString());
	}

	public String nameP(Integer id) {
		return prodao.findById(id).get().getName();
	}

	// format currency
	public String format(String number) {
		DecimalFormat formatter = new DecimalFormat("###,###,###.##");

		return formatter.format(Double.valueOf(number)) + " VNĐ";
	}

	@RequestMapping("/admin/order/sent/{order_id}")
	public String sendMail(Model model, @PathVariable("order_id") Integer order_id) {
		Order ord = odao.getById(order_id);
		String subject = "#" + ord.getOrder_id() + " - Thông báo cập nhật trạng thái đơn hàng!";
		sendMailAction(ord, subject);
		model.addAttribute("message", "Gửi mail thành công");
		return "redirect:/admin/order/edit?order_id=" + order_id;
	}

}
