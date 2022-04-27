package ar.com.intrale;

import ar.com.intrale.messages.Response;

public class DownloadResponse extends Response {

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
