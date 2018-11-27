package com.example.demo.objects;

public class ImpresionInfo {
	String message;
	int count;

	public ImpresionInfo(String message, int count) {
		super();
		this.message = message;
		this.count = count;
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
