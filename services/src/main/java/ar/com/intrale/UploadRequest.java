package ar.com.intrale;

import ar.com.intrale.messages.RequestRoot;

public class UploadRequest extends RequestRoot {

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
