package beta2.engines;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import beta2.myclass.SConfig;
import beta2.myclass.Webpage;

public class Baidu extends Engine {
	private Map<String, String> CMap;
	private static Logger logger = LogManager.getLogger(Engine.class.getName());
	private String baiduConf =SConfig.get("baiduconf");

	public Baidu() {
		// TODO Auto-generated constructor stub
		try {
			this.CMap = super.readConf(baiduConf);
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
		Map<String, String> searchParam = new HashMap<String, String>();
		searchParam.put("num", "15");
		searchParam.put("keyword", "袁军辉");
//		searchParam.put("time", "30");
		EngineFactory efac=new EngineFactory();
		Engine baidu=efac.getEngine("baidu");
		for (Map<String, String> tmap : baidu.getResults(searchParam))
			System.out.println(tmap);
	}

	/*
	 * 参数: 参数：map{page,keyword,site}
	 */
	String getUrl(Map<String, String> searchParam) {
		// TODO Auto-generated method stub
		String urlParam = "&ie=utf-8&ct=0";
		if (searchParam.containsKey("keyword")&&searchParam.get("keyword").length() > 0) {
			urlParam += "&wd=" + searchParam.get("keyword");
		} else {
			logger.error("关键词为空，返回null");
			return null;
		}
		
		if (searchParam.containsKey("page")&&searchParam.get("page").length() > 0) {
			urlParam += "&pn=" + searchParam.get("page");
		} 

		if(searchParam.containsKey("time")&&searchParam.get("time").length()>0){
			urlParam+="&lm="+searchParam.get("time");
		}
		return CMap.get("baseurl") + urlParam.substring(1);
	}

	/*
	 * 参数: 参数：map{num,keyword,site}
	 */
	@Override
	ArrayList<Map<String, String>> getResults(
			Map<String, String> searchParam) {
		// TODO Auto-generated method stub
		ArrayList<Map<String, String>> results = new ArrayList<Map<String, String>>();
		// 参数传入
		int perpage = Integer.parseInt(CMap.get("perpage"));
		Integer count=Integer.parseInt(searchParam.get("num"));
		
		String url;
		Webpage webpage = null;
		webpage = new Webpage(CMap);
		for (int page = 1; page <= Math.ceil((float) count / perpage); page++) {
			searchParam.put("page", Integer.toString(page));
			url = getUrl(searchParam);
			logger.debug("提取url:" + url);
			super.resultsCombine(results, webpage.getItemsInfo(url), "", "");

		}
		for (Map<String, String> map : results) {
			for (String key : map.keySet()) {
				map.put(key,
						map.get(key).replaceAll("</?[a-zA-Z!\\-]+[^><]*>", ""));
			}
			map.put("engine", this.CMap.get("name"));
		}
		return results;
	}

}
