package beta2.proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import beta2.metasearch.MetaSearch;

public class MProxy {
	private static Logger logger = LogManager.getLogger(MetaSearch.class
			.getName());
	private Map<Integer, String> IpMap;
	private int maxIp;
	private int proxyTimes;
	String proxyFile = "conf/ipproxy.conf";
	int URLTIMEOUT = 8000;
	String TESTURL = "http://www.baidu.com/";
	String TESTURLCHARSET = "utf-8";

	
	public MProxy() {
		// TODO Auto-generated constructor stub
		proxyTimes = 0;
		IpMap = new HashMap<Integer, String>();
		try {
			this.load(proxyFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 设置代理服务

		String content = "", line = "";
		String purl = "http://www.baidu.com/s?ie=utf-8&bs=adsf&f=8&rsv_bp=1&rsv_spt=3&wd=IP&rsv_sug3=2&inputT=695";
		int urlTimeout = 15000;
		String charset = "utf-8";
		try {
			URL url = new URL(purl);
			SocketAddress add = new InetSocketAddress("110.4.12.170", 83);
			Proxy p = new Proxy(Proxy.Type.HTTP, add);
			URLConnection turl = url.openConnection(p);

			turl.setConnectTimeout(urlTimeout);
			turl.setReadTimeout(urlTimeout);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					turl.getInputStream(), charset));
			while ((line = in.readLine()) != null) {
				content += line;
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(content);
	
	}

	public Proxy getProxy(String url) {
		String proxy = IpMap.get(proxyTimes++ % maxIp + 1);
		String[] proxyInfo = proxy.split(":", 2);
		SocketAddress add = new InetSocketAddress(proxyInfo[0],
				Integer.parseInt(proxyInfo[1]));
		logger.debug("proxy:" + proxy);
		return (new Proxy(Proxy.Type.HTTP, add));
	}

	private boolean load(String ipFile) throws IOException {
		Set<String> ipset = new HashSet<String>();
		this.maxIp = 1;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(ipFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		String line = null;

		while ((line = br.readLine()) != null) {
			line = line.trim();
			// 过滤注释及不规范内容
			if (!line.startsWith(";") && line.contains(":")
					&& !ipset.contains(line)) {

				IpMap.put(maxIp++, line);
				ipset.add(line);
			}
		}
		br.close();
		return true;

	}

	

}
