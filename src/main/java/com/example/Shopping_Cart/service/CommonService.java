package com.example.Shopping_Cart.service;

public interface CommonService {

	public void removeSessionMessage();

	public void sendMail(String url, String recipientEmail);
}