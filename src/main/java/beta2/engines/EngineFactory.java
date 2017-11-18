package beta2.engines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EngineFactory {
	Map<String, Engine> engines;

	public EngineFactory() {
		// TODO Auto-generated constructor stub
		engines = new HashMap<String, Engine>();
		engines.put("baidu", new Baidu());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	// 获取搜索引擎实例
	public Engine getEngine(String engineName) {
		Engine engine=null;
		if (engines.containsKey(engineName)) {
			engine= engines.get(engineName);
		}
		return engine;

	}
}
