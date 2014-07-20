import static org.junit.Assert.*;

import ivy.basic.AppException;
import ivy.core.tool.Str;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.sun.tools.xjc.model.CCustomizable;

import chong.CCUtil;
import chong.ChongChong;
import chong.ReadList;

/**
 */
public class ChongChongTest {

	private String CHROME_USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36";

	private String url = "http://www.gangqinpu.com/html/2999.htm";

	private String song = "/Ajax/pudata.aspx?songid";
	private String site = "http://www.gangqinpu.com";
	private String site1 = "st.aspnethome.cn";
	private String site2 = "st2.gangqinpu.com";

	private String type = "钢琴";

	private HttpClient client;

	@Test
	public void test() throws AppException {
		List<ChongChong> list = ReadList.read();
		list = buildcc();
		System.out.println("共读取" + list.size() + "个网页");
		for (ChongChong cc : list) {
			findMp3(cc);
		}
		// findMp3(buildCC());
	}

	private List<ChongChong> buildcc() {
		List<ChongChong> list = new ArrayList<ChongChong>();
		list.add(new ChongChong("故乡的原风景钢琴谱-森海岸原",
				"http://www.gangqinpu.com/html/18559.htm"));
		list.add(new ChongChong("故乡的原风景 -FO-A",
				"http://www.gangqinpu.com/html/11282.htm"));
		return list;
	}

	public ChongChong buildCC() {
		ChongChong cc = new ChongChong();
		cc.setName("保卫黄河!");
		cc.setUrl(url);
		return cc;
	}

	public void findMp3(ChongChong cc) {
		try {
			String http = http(cc);
			if (Str.isEmpty(http)) {
				System.out.println("HTTP 返回结果 为空!");
			} else {
				int index = http.indexOf(song);
				if (index > 0) {
					String value = http.substring(index);
					index = value.indexOf("\"");
					if (index > 0) {
						value = value.substring(0, index);
						value = site + value;
						System.out.println("URL:" + value);
						value = http2(value, cc);
						System.out.println(value);
						findFlashURL(value, cc);
					} else {
						System.out.println("没有找到");
					}
				} else {
					System.out.println("没有找到歌曲!");
				}
			}
		} catch (AppException e) {
			System.out.println("ERROR" + cc.getName());
			e.printStackTrace();
		}
	}

	public void findFlashURL(String value, ChongChong cc) throws AppException {
		int index = value.indexOf("swflash");
		if (index > 0) {
			value = value.substring(value.indexOf("iAuther"));
			String[] values = value.split("player_str");
			int length = values.length;
			String link = "";
			switch (length) {
			case 3:
				link = matchmp3URL(value);
				save(link, getMp3FileName(cc, null));
				break;
			case 5:
				System.out.println("原版试听:");
				link = matchmp3URL(values[0]);
				save(link, getMp3FileName(cc, "11"));
				System.out.println("钢琴试听:");
				link = matchmp3URL(values[2]);
				save(link, getMp3FileName(cc, null));
				break;
			default:
				throw new AppException("没有找到FLASH2");
			}
		} else {
			throw new AppException("没有找到FLASH1");
		}
	}

	public String getMp3FileName(ChongChong cc, String type) {

		String filename = cc.getName();
		filename = filename.replaceAll(" ", "") + ".mp3";
		if (Str.isNotEmpty(type)) {
			filename = "[原版]" + filename;
		}
		return filename;
	}

	public void save(String link, String filename) throws AppException {
		HttpClient client = getClient();
		HttpGet request = new HttpGet(link);
		try {
			HttpResponse response = client.execute(request, httpContext);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				long contentLength = entity.getContentLength();
				Double size = contentLength / 1024.0 / 1024.0;
				System.out.println("准备下载文件大小:" + String.format("%1$3.2f", size)
						+ "MB");
				InputStream inputStream = entity.getContent();
				CCUtil.save(inputStream, filename, type);
				return;
			} else {
				throw new AppException("下载MP3失败,返回HTTP-CODE:"
						+ response.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new AppException("Download MP3 Failed!");
	}

	public String matchmp3URL(String value) throws AppException {
		String[] values = value.split("http", 2);
		if (values.length == 2) {
			value = values[1];
		} else {
			throw new AppException("没找到HTTP链接");
		}
		value = value.substring(0, value.indexOf("'"));
		value = "http" + value;
		value = value.replaceAll(Pattern.quote(site1), site2);
		System.out.println("最终URL:\n" + value);
		return value;
	}

	public String http2(String url, ChongChong cc) throws AppException {
		HttpGet request = new HttpGet(url);
		try {
			String value = runAsString(request);
			return value;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new AppException("HTTP2 ERROR:" + cc.getName() + "\n"
				+ cc.getUrl());
	}

	public synchronized HttpClient getClient() {
		if (client == null)
			client = HttpClients.custom().setUserAgent(CHROME_USER_AGENT)
					.build();
		return client;
	}

	public HttpResponse run(HttpUriRequest request)
			throws ClientProtocolException, IOException {
		HttpClient client = getClient();
		HttpResponse response = client.execute(request, httpContext);
		return response;
	}

	public String runAsString(HttpUriRequest request)
			throws ClientProtocolException, IOException {
		HttpResponse response = run(request);
		String value = EntityUtils.toString(response.getEntity(), "gb2312");
		return value;
	}

	public String http(ChongChong cc) throws AppException {
		HttpGet request = new HttpGet(cc.getUrl());
		try {
			return runAsString(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new AppException("HTTP ERROR:" + cc.getName() + "\n"
				+ cc.getUrl());
	}

	public static CookieStore cookieStore = new BasicCookieStore();
	public static HttpContext httpContext = new BasicHttpContext();
	static {
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
	}
}
