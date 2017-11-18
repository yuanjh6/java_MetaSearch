package beta2.myclass;

/*
 * 获取网页内容
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import beta2.proxy.MProxy;

public class Webpage {
	private static Logger logger = LogManager
			.getLogger(Webpage.class.getName());
	private static int urlTimeout = 8000;
	private Map<String, String> cMap;
	private String charset, headTag, footTag, splitTag, regexp, regexpInfo;
	private MProxy mproxy;

	
	public Webpage(Map<String, String> cMap) {
		this.cMap = cMap;
		charset = cMap.get("charset");
		headTag = cMap.get("headtag");
		footTag = cMap.get("foottag");
		splitTag = cMap.get("splittag");
		regexp = cMap.get("regexp");
		regexpInfo = cMap.get("regexpinfo");
		mproxy=new MProxy();
	}

	public ArrayList<String> getItems(String purl) {

		String content = this.getContent(purl);
		if (!checkStr(content)) {
			logger.error("获取网页为空 ，返回null");
			return null;
		}
		ArrayList<String> Items = new ArrayList<String>();

		if (!checkStr(splitTag)) {
			logger.debug("切分标错误，返回null");
			return null;
		}
		splitTag = splitTag.trim();
		String[] tItems = content.split(splitTag);

		for (String s : tItems) {
			if (checkStr(s)) {
				Items.add(s);
			}
		}

		return Items;
	}

	public String getContent(String purl) {
		String content;
		content = this.getPage(purl);

		if (!checkStr(content)) {
			logger.error("获取网页为空 ，返回null");
			return null;
		}
		String tcontent[];
		if (checkStr(headTag)) {
			tcontent = content.split(headTag, 2);
			if (tcontent.length < 2 || !checkStr(tcontent[1])) {
				logger.debug("头部标签切分失败 ，忽略 ");
			} else {
				content = tcontent[1];
			}
		}
		if (checkStr(footTag)) {
			tcontent = content.split(footTag, 2);
			if (tcontent.length < 2 || !checkStr(tcontent[0])) {
				logger.debug("尾部标签切分失败 ，忽略 ");
			} else {
				content = tcontent[0];
			}
		}

		return content;
	}

	public ArrayList<Map<String, String>> getItemsInfo(String purl) {
		ArrayList<String> Items = this.getItems(purl);
		if (Items == null) {
			logger.error("条目过去失败，返回null");
			return null;
		}
		ArrayList<Map<String, String>> ItemsInfo = new ArrayList<Map<String, String>>();
		Map<String, String> tItem;
		if (!checkStr(regexp) || !checkStr(regexpInfo)) {
			logger.error("正则式错误，返回Null");
			return null;
		}
		Regexp mregexp = new Regexp(regexp, regexpInfo);
		int bad = 0, good = 0;

		for (String s : Items) {
			if (checkStr(s)) {
				tItem = mregexp.getInfo(s);
				if (tItem != null) {
					ItemsInfo.add(tItem);
					good++;
				} else {
					bad++;
				}
			}
		}
		logger.info("提取成功数量" + good + " 提取失败条目数量:" + bad);
		return ItemsInfo;
	}

	/*
	 * 读入配置文件 配置文件中以;开头为注释文件 格式如:name=**
	 */
	private Map<String, String> readConfig(String file) {
		BufferedReader rd;
		String line;
		String[] s = new String[2];
		Map<String, String> map = new HashMap<String, String>();
		try {
			rd = new BufferedReader(new FileReader(file));
			while ((line = rd.readLine()) != null) {
				if (!line.startsWith(";")) {
					s = line.split("=", 2);
					if (s.length < 2 || !checkStr(s[0]))
						continue;
					map.put(s[0].trim(), s[1].trim());
				}
			}
			rd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("读入配置文件错误，退出 ");
			e.printStackTrace();
			return null;
		}
		return map;
	}

	public String getPage(String purl) {
		if (!checkStr(purl)) {
			logger.error("参数非法,返回null");
			return null;
		}
		if (!checkStr(charset)) {
			charset = this.getCharset(purl);
			if (!checkStr(charset)) {
				logger.error("网页编码获取失败,返回null");
				return null;
			}
		}
		purl = this.format(purl);

		String content = "", line = "";
		try {
			URL url = new URL(purl);
			URLConnection turl = url.openConnection(mproxy.getProxy(purl));

			turl.setConnectTimeout(urlTimeout);
			turl.setReadTimeout(urlTimeout);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					turl.getInputStream(), charset));
			while ((line = in.readLine()) != null) {
				content += line;
			}
			in.close();
		} catch (IOException e) {
			logger.error("获取网页内容出错，返回null");
			return null;
		}
		return content;
	}

	private String getCharset(String purl) {
		String content = "", line, ret = null;
		try {
			URL url = new URL(purl);
			URLConnection turl = url.openConnection(mproxy.getProxy(purl));
			turl.setConnectTimeout(urlTimeout);
			turl.setReadTimeout(urlTimeout);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					turl.getInputStream()));

			while ((line = in.readLine()) != null) {
				content += line;
			}
			in.close();

			int j = content.indexOf("charset=") + 8;

			if (content.length() < j) {
				logger.error("无法获取网页字符集 ，页面内容获取失败，返回null");
				return null;
			}
			String Scharset = content.substring(j, j + 8);
			if (Scharset.startsWith("utf-8") || Scharset.startsWith("UTF-8")) {
				ret = "utf-8";
			} else if (Scharset.startsWith("gbk") || Scharset.startsWith("GBK")) {
				ret = "gbk";
			} else if (Scharset.startsWith("gb2312")
					|| Scharset.startsWith("GB2312")) {
				ret = "gb2312";
			}

		} catch (IOException e) {
			logger.error("获取网页字符失败 ，返回null");
			return null;
		}
		return ret;
	}

	/*
	 * 返回配置文件中的对应的值
	 */
	public String getConfig(String key) {
		return cMap.get(key);
	}

	/*
	 * 检查字符串是否合法，（不为空并且除去收尾空白字符后长度大于o），合法返回true
	 */
	public static boolean checkStr(String str) {
		if (str == null || str.trim().length() <= 0) {
			return false;
		}
		return true;
	}

	private String format(String url) {
		if (!(url.startsWith("http://"))) {
			url = "http://" + url;
		}
		return url;
	}
}
