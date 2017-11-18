package beta2.myclass;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SConfig {
	/*
	 * 配置文件：从配置文件路径得到配置文件，读取配置文件的内容得到配置信息，集中系统所有配置信息 文件格式为 key=value
	 */
	static private Logger logger = LogManager.getLogger(SConfig.class.getName());
	
	static private String configFile="conf/metasearch.conf";
	static private HashMap<String, String> confMap;

	/*
	 * 加载配置文件，将配置文件读入静态的类变量Map当中
	 */
	static{
		confMap = new HashMap<String, String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(configFile));
			String line;
			String[] t;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(";"))
					continue;
				if (!checkStr(line))
					continue;

				t = line.split("=",2);
				if (!checkStr(t[0])) {
					logger.warn("配置文件key值读入故障，跳过");
					continue;
				}
				t[0] = t[0].trim();
				if (!checkStr(t[1])) {
					t[1] = "";
				}
				confMap.put(t[0], t[1]);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("配置文件路径错误，配置文件不存在");
			e.printStackTrace();
			
		}
		
	}

	static public String get(String key) {
		String value = null;
		if (confMap.containsKey(key))
			value = confMap.get(key);
		return value;
	}
	
	/*
	 * 检查字符串是否合法，（不为空并且除去收尾空白字符后长度大于o），合法返回true
	 */
	static public boolean checkStr(String str) {
		if (str == null)
			return false;
		str = str.trim();
		if (str.length() <= 0)
			return false;
		return true;
	}

}
