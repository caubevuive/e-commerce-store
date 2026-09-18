package com.example.Shopping_Cart.service;

public interface CommonService {

	public void removeSessionMessage();

	public Boolean sendMail(String url, String recipientEmail);
}