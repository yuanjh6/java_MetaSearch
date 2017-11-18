package beta2.metasearch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jeasy.analysis.MMAnalyzer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MetaSearch {
	private static Logger logger = LogManager.getLogger(MetaSearch.class
			.getName());
	static MMAnalyzer analyzer = new MMAnalyzer();

	public MetaSearch() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<String, String>();
		map.put("intitle", "");
		map.put("inurl", "");
		map = queryFormat("火影很好 intitle:new3w.com inurl:nihaoo", map, true,
				"and");
		System.out.println(map);
		map = queryFormat("火影很好 intitle:new3w.com inurl:nihaoo", map);
		System.out.println(map);
	}

	protected static Map<String, String> queryFormat(String query,
			Map<String, String> param, boolean analyze, String defaultLogic) {
		Map<String, String> searchParam = queryFormat(query,param);

		String unHandleQuery = searchParam.get("query");
		// 根据参数判断是对query进行分词并构造逻辑表达式
		if (analyze) {
			try {
				String logic;
				if (defaultLogic.equalsIgnoreCase("and")) {
					logic = " AND ";
				} else if (defaultLogic.equalsIgnoreCase("or")) {
					logic=" OR  ";
				} else if (defaultLogic.equalsIgnoreCase("not")) {
					logic=" NOT ";
				}else{
					logger.debug("默认逻辑关系为空,设置为or");
					logic=" OR  ";
				}

				unHandleQuery = analyzer.segment(unHandleQuery, logic);
				//去除尾部多余6个字符
				unHandleQuery=unHandleQuery.substring(0, unHandleQuery.length()-5);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		searchParam.put("query",unHandleQuery );
		return searchParam;
	}
	
	protected static Map<String, String> queryFormat(String query,
			Map<String, String> param) {
		Map<String, String> searchParam = null;
		if (query == null || query.length() == 0) {
			logger.error("query不能为空");
			return searchParam;
		}
		searchParam = new HashMap<String, String>();
		String unHandle = "";
		String[] subQueryValue;
		for (String subQuery : query.split(" ")) {
			subQueryValue = subQuery.split(":|：", 2);
			// 不含分隔符:或：,所以一定不包含参数,无需处理
			if (subQueryValue.length < 2) {
				unHandle += " " + subQuery;
				continue;
			} else {
				subQueryValue[0] = subQueryValue[0].trim();
				subQueryValue[1] = subQueryValue[1].trim();
				// 是否是参数
				if (param.containsKey(subQueryValue[0])) {
					searchParam.put(subQueryValue[0], subQueryValue[1]);
				} else {
					unHandle += " " + subQuery;
				}
			}


		}
		//去除头部空格字符后保存
		searchParam.put("query",unHandle.substring(1).trim() );
		return searchParam;
	}
	
	
	

	private String queryLogic(String query, boolean queryLogic) {
		String logicQuery = null;
		if (query == null) {
			logger.error("query为空");
			return logicQuery;
		}
		return logicQuery;
	}

}
