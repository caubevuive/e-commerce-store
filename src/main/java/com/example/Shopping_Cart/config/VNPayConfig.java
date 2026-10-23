package com.example.Shopping_Cart.config;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VNPayConfig {
	public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
	public static String vnp_ReturnUrl = "http://localhost:8080/vnpay_payment_return"; // URL nhận kết quả trả về
	public static String vnp_TmnCode = "2QXUI4J4"; // Mã test sandbox mặc định của VNPay (hoặc mã của bạn)
	public static String vnp_HashSecret = "AELXKYZOZUJJOSGWSJXZYQWROZAOEWZZ"; // Khóa bí mật test sandbox mặc định

	// Hàm mã hóa bảo mật chữ ký
	public static String hmacSHA512(final String key, final String data) {
		try {
			if (key == null || data == null) {
				throw new NullPointerException();
			}
			final Mac hmac512 = Mac.getInstance("HmacSHA512");
			byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
			final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
			hmac512.init(secretKey);
			byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
			byte[] result = hmac512.doFinal(dataBytes);
			StringBuilder sb = new StringBuilder(2 * result.length);
			for (byte b : result) {
				sb.append(String.format("%02x", b & 0xff));
			}
			return sb.toString();
		} catch (Exception ex) {
			return "";
		}
	}

	// Hàm tạo mã giao dịch ngẫu nhiên
	public static String getRandomNumber(int len) {
		Random rnd = new Random();
		String chars = "0123456789";
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(chars.charAt(rnd.nextInt(chars.length())));
		}
		return sb.toString();
	}
}