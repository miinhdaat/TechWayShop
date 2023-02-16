package com.poly.service.impl;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.dao.AccountDao;
import com.poly.dao.OrderDao;
import com.poly.dao.OrderDetailDao;
import com.poly.dao.ProductDao;
import com.poly.entity.Account;
import com.poly.entity.Order;
import com.poly.entity.OrderDetail;
import com.poly.service.OrderService;
import com.poly.service.SendMailService;

@Service
public class OrderServiceImlq implements OrderService {
	@Autowired
	OrderDao dao;
	@Autowired
	AccountDao adao;
	@Autowired
	OrderDetailDao ddao;
	@Autowired
	ProductDao pdao;
	@Autowired
	SendMailService service;

	@Override
	public Order create(JsonNode orderData) {
		ObjectMapper mapper = new ObjectMapper();
		Order order = mapper.convertValue(orderData, Order.class);
		dao.save(order);
		TypeReference<List<OrderDetail>> type = new TypeReference<List<OrderDetail>>() {
		};
		List<OrderDetail> details = mapper.convertValue(orderData.get("orderDetails"), type).stream()
				.peek(d -> d.setOrder(order)).collect(Collectors.toList());
		ddao.saveAll(details);
		try {
			sendMailAction(order, "#" + order.getOrder_id() + " - Thông báo đặt hàng thành công");
			System.out.println("SEND MAIL!");
		} catch (Exception e) {
			System.out.println("ERROR WITH SEND MAIL!");
			e.printStackTrace();
		}
		return order;
	}

	@Override
	public Order findById(Integer id) {
		return dao.findById(id).get();
	}

	@Override
	public List<Order> findByUsername(String username) {
		return dao.findByUsername(username);
	}

	@Override
	public List<Order> findByAllDesc() {
		return dao.findByAllDesc();
	}

	@Override
	public void deleteById(Integer id) {
		dao.deleteById(id);
	}

	@Override
	public Order update(Order order) {
		return dao.save(order);
	}

	public void sendMailAction(Order oReal, String subject) {
		List<OrderDetail> list = ddao.findByOrderID(oReal.getOrder_id());
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
				+ "<p>Cửa hàng xin thông báo đã nhận được đơn đặt hàng mang mã số #" + oReal.getOrder_id()
				+ " của bạn.</p>\r\n"
				+ "<p>Đơn hàng của bạn đang được tiếp nhận và trong quá trình xử lí. Dưới đây là thông tin đơn hàng.</p>\r\n"
				+ "<p>Trạng thái đơn hàng: " + status + ".</p>\r\n" + "<p>http://localhost:8888/order/detail/"
				+ oReal.getOrder_id() + "</p>\r\n" + "<hr>\r\n" + "<h4>CHI TIẾT ĐƠN HÀNG</h4>"
				+ "    <table style=\"border: 1px solid gray;\">\r\n"
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
		String hi = "hi";
		service.queue(acc.getEmail(), subject, stringBuilder.toString());
	}

	public String nameP(Integer id) {
		return pdao.findById(id).get().getName();
	}

	// format currency
	public String format(String number) {
		DecimalFormat formatter = new DecimalFormat("###,###,###.##");

		return formatter.format(Double.valueOf(number)) + " VNĐ";
	}

}