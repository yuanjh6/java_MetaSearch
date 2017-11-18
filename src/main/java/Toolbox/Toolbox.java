package Toolbox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Toolbox {
	String proxyFile = "conf/ipproxy.conf";
	int URLTIMEOUT = 4000;
	String TESTURL = "http://www.baidu.com/";
	String TESTURLCHARSET = "utf-8";

	public Toolbox() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Toolbox toolbox=new Toolbox();
//		toolbox.getProxy("conf/ip");
		if(toolbox.testProxy("conf/ip")){
			System.out.println("ok");
		}else{
			System.out.println("false");
		};
		
	}
	
	private boolean getProxy(String file){
		ArrayList<Map<String,String>> ALMap=new ArrayList<Map<String,String>>();
		//参数保存
		Map<String,String> map=new HashMap<String,String>();
		map.put("url", "http://pachong.org/");
		map.put("charset", "utf-8");
		map.put("headtag", "<div class=\"mainWap\">");
		map.put("foottag", "<div class=\"natWap clear\">");
		map.put("splittag", "<tr>");
		map.put("regexp", ".*?</td>.*?<td>(.*?)</td>.*?<td>(.*?)</td>");
		map.put("regexpinfo", "ip:1,port:2");
		ALMap.add(map);
		
		map=new HashMap<String,String>();
		map.put("url","http://51dai.li/http_fast.html");
		map.put("charset", "utf-8");
		map.put("headtag", "<div id=\"container\">");
		map.put("foottag", "</table>");
		map.put("splittag", "</tr>");
		map.put("regexp", ".*?</td>.*?<td>(.*?)</td>.*?<td.*?>(.*?)</td>");
		map.put("regexpinfo", "ip:1,port:2");
		ALMap.add(map);
		
		map=new HashMap<String,String>();
		map.put("url","http://51dai.li/http_anonymous.html");
		map.put("charset", "utf-8");
		map.put("headtag", "<div id=\"container\">");
		map.put("foottag", "</table>");
		map.put("splittag", "</tr>");
		map.put("regexp", ".*?</td>.*?<td>(.*?)</td>.*?<td.*?>(.*?)</td>");
		map.put("regexpinfo", "ip:1,port:2");
		ALMap.add(map);
		
		map = new HashMap<String, String>();
		map.put("url", "http://www.proxy360.cn/Region/China");
		map.put("charset", "utf-8");
		map.put("headtag", "<div style=\"color:Blue; font-weight:bold;\">");
		map.put("foottag", "<div id=\"ctl00_ContentPlaceHolder1_repProxyList3_ctl502_RatingSpeed\" title=\"5\">");
		map.put("splittag", "<div class=\"proxylistitem\" name=\"list_proxy_ip\">");
		map.put("regexp", ".*?<span.*?>(.*?)</span>.*?<span.*?>(.*?)</span>");
		map.put("regexpinfo", "ip:1,port:2");
		ALMap.add(map);
		
		String charset,headTag,footTag,splitTag,regexp,regexpInfo;
		Webpage webpage;
		ArrayList<Map<String, String>> ItemsInfo = new ArrayList<Map<String, String>>();
		for(Map<String,String> cMap:ALMap){
			webpage=new Webpage(cMap);
			ItemsInfo.addAll(webpage.getItemsInfo(cMap.get("url")));
		}
		
		BufferedWriter br;
		try {
			br = new BufferedWriter(new FileWriter(file));
			for(Map<String,String> tmap:ItemsInfo){
				br.write(tmap.get("ip").trim()+":"+tmap.get("port").trim()+"\n");
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
		return true;
	}
	
	private boolean testProxy(String ipFile) {
		Set<String> ipset = new HashSet<String>();
		int maxIp = 1;
		Map<Integer, String> IpMap=new HashMap<Integer,String>();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(ipFile));
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		String proxy;
		String purl = TESTURL;
		int urlTimeout = URLTIMEOUT;
		String charset = TESTURLCHARSET;

		Iterator<Map.Entry<Integer, String>> it = IpMap.entrySet().iterator();
		while (it.hasNext()) {
			// 获取代理服务器
			Map.Entry<Integer, String> entry = it.next();
			proxy = entry.getValue();
			String[] proxyInfo = proxy.split(":", 2);
			if(proxyInfo.length<2)continue;
			SocketAddress add = new InetSocketAddress(proxyInfo[0],
					Integer.parseInt(proxyInfo[1]));

			URLConnection turl;
			URL url;
			try {
				url = new URL(purl);
				turl = url.openConnection(new Proxy(Proxy.Type.HTTP, add));
				turl.setConnectTimeout(urlTimeout);
				turl.setReadTimeout(urlTimeout);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						turl.getInputStream(), charset));
			} catch (IOException e1) {
				// 一旦出错认为代理服务器无效
				e1.printStackTrace();
				it.remove();
				continue;
			}

		}

		// 旧代理文件改名
		File file = new File(ipFile);
		File newfile=new File(ipFile+".old");
		if(newfile.exists()){newfile.delete();}
		file.renameTo(newfile);
		
		// 写入新代理文件
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(ipFile));
			for (Map.Entry<Integer, String> entry : IpMap.entrySet()) {
				bw.write(entry.getValue()+"\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
	

}
