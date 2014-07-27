package chong;

import ivy.core.tool.Str;

/**
 */
public class ChongChong {
	private String name;
	private String url;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (Str.isNotEmpty(url)) {
			if (!url.contains(":")) {
				url = "http://www.gangqinpu.com/html/" + url + ".htm";
			}
		}
		this.url = url;
	}

	public ChongChong() {
		super();
	}

	public ChongChong(String name, String url) {
		super();
		this.name = name;
		setUrl(url);
	}

	public ChongChong(String url) {
		super();
		setUrl(url);
	}
}
