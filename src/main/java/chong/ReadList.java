package chong;

import ivy.basic.AppException;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

/**
 */
public class ReadList {

	private static String url = "d:/1231/bookmarks_14-7-20.html";

	public static List<ChongChong> read() throws AppException {
		try {
			List<ChongChong> list = new ArrayList<ChongChong>();
			Parser parse = new Parser(url);
			NodeFilter filterP = new TagNameFilter("A");// 过滤这个标签
			NodeList nodeList = parse.extractAllNodesThatMatch(filterP);
			SimpleNodeIterator elements = nodeList.elements();
			while (elements.hasMoreNodes()) {
				Node nextNode = elements.nextNode();
				if (nextNode instanceof LinkTag) {
					LinkTag link = (LinkTag) nextNode;
					String url = link.getLink();
					if (url.endsWith("htm")) {
						String name = link.getChildrenHTML();
						name = name.replaceAll("-虫虫钢琴谱免费下载", "");
						System.out.println(name + "\t" + url);
						ChongChong cc = new ChongChong();
						cc.setName(name);
						cc.setUrl(url);
						list.add(cc);
					}
				}
			}
			return list;
		} catch (ParserException e) {
			e.printStackTrace();
		}
		throw new AppException("读取书签失败!");
	}
}
