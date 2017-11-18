package beta2.engines;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import beta1.Config;

import MyClass.Webpage;

public abstract class Engine {
	
	abstract String getUrl(Map<String,String> searchParam);
	abstract ArrayList<Map<String, String>> getResults(Map<String,String> searchParam);
	private static Logger logger = LogManager.getLogger(Engine.class.getName());
	static protected boolean resultsCombine(ArrayList<Map<String,String>> A,ArrayList<Map<String,String>> B,String key,String distance){
		ArrayList<Map<String,String>> results=new ArrayList<Map<String,String>>();
		return A.addAll(B);
	}
	
	protected static Map<String,String>  readConf(String configFile) throws IOException{
		Map<String,String> map=new HashMap<String,String>();
		
		BufferedReader br =null;
		br=new BufferedReader(new FileReader(configFile));
		String line;
		String[] tline;
		while ((line = br.readLine()) != null) {
			if (line.startsWith(";")||line.trim().length()==0)
				continue;
			tline = line.split("=",2);
			if (tline[0].trim().length()==0) {
				continue;
			}
			tline[0] = tline[0].trim();
			if (tline[1].trim().length()==0) {
				tline[1] = "";
			}
			map.put(tline[0], tline[1]);
		}
		br.close();
		return map;
	};
}
