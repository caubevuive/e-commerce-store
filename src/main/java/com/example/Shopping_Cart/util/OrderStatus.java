package com.example.Shopping_Cart.util;

public enum OrderStatus {

	PENDING(0, "Pending"), IN_PROGRESS(1, "In Progress"), DELIVERED(2, "Delivered"), CANCELLED(3, "Cancelled");

	private final Integer id;
	private final String name;

	OrderStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static String getValue(Integer id) {
		for (OrderStatus st : OrderStatus.values()) {
			if (st.getId().equals(id)) {
				return st.getName();
			}
		}
		return "Pending";
	}
}