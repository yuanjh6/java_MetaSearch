元搜索工具（半成品）
元搜索，从不同搜索引擎爬取搜索结果，去重，重排序后聚合为新结果
优点：搜索结果一般较单一搜索引擎更佳（类似数据挖掘中的模型融合）。广告会去掉。避免针对特定搜索引擎的过分优化导致的异常排名
缺点：需要从不同搜索引擎提取结果，返回结果较慢。去重仅能做到标题去重（大部分搜索引擎也仅如此，否则也不会出现很多网页打开都一样）
代码简述：
虽然代码看起来是maven项目，实际是普通java项目，没有maven支持，需要自己导入jar包

java/toolbox:
Webpage：网页提取类，使用map初始化，map中传入网页切分规则，以及charset, headTag, footTag, splitTag, regexp, regexpInfo等信息。
每个搜索引擎只是webpage对象中的初始参数不同，其他都是一样的。传入url地址后可直接获取 ArrayList<Map<String, String>>类型的搜索结果。
Regexp:正则表达式工具类
toolbox：工具类，获取代理服务器，测试代理服务器

java/beta2/engines
baidu：engine的扩展，调用sconfig读取自己的引擎配置，对外提供直接获取搜索结果的ArrayList<Map<String, String>> getResults接口。
engine:抽象类，配置文件转map形式提供给enginefactory对象。以及公共的方法，搜索参数获取url或结果连接等。

java/beta2/myclass
SConfig:封装了读取xml文件，只需要读取一次，后续缓存到变量中
java/beta2/proxy
Mproxy：代理类，从配置文件读取代理配置，供上层调用，返回Proxy对象。

java/beta2/metasearch
这个只实现了片段，对搜索词进行分词，然后使用默认的连接词进行连接（and or not，大部分搜索引擎支持逻辑连接词），
还需要对结果进行拼接，去重（标题去重，链接url去重，网页摘要相似度去重等），排序（需要好考虑不同引擎的权值）等等，还是比较复杂的。




