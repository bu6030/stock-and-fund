# 股基神器

[功能](#jump1)  
[功能描述](#jump2)  
[修改历史](#jump3)  
[UI页面](#jump4)  
[部署](#jump5)  
[初始化表结构](#jump6)  

## <span id="jump1">功能</span>
1. 可以模拟炒股哦
2. 手动添加股票基金
3. 查看股票基金实时价格
4. 实时计算显示收益亏损，总仓位，市值，各个股票基金持仓占比
5. 定时统计每日收益
6. 查看每日收益汇总，每月收益汇总，每年收益汇总
7. 唐安奇通道法数据监控股票
8. 支持多用户登录

## <span id="jump2">功能描述</span>
自己研究写的一个股票基金SpringBoot小项目，我给它命名股基神器，可以模拟炒股哦！  
我自己有多个APP购买股票基金的情况，每次还要到多个APP查看，很费劲  
因此自己写了个汇总的，只支持中国股票基金，录入基金股票编码，购买成本，持仓数量来添加股票基金，便可以实时查看价格以及盈亏
可实时查看股票基金实时价格，周一至周五交易时间定时刷新  
单独查看股票或者基金实时价格，以及汇总股票基金实时价格  
添加删除股票基金  
买入卖出股票以及按照买卖后的结果计算展示当日收益
查看历史盈亏记录，可按周月年筛选统计  
周一至周五下午3点30自动统计当日盈亏  
手动添加股票基金APP，例如东方财富，支付宝，微信等  
多个页面UI，Bootstrap以及Layui样式  
支持多用户登录，需要在 users 表和 authorities 表中增加账号密码数据就可以登录了，密码可以使用com.buxuesong.account.infrastructure.general.utils.PasswordUtils来生成密码  
具体内容们可以查看我的帖子：<https://zhuanlan.zhihu.com/p/557316975>  

## <span id="jump3">修改历史</span>
1. 增加30s自动刷新 By bu6030 2022/8/1 15:57  
2. 修改为开盘时间自动刷新，非交易时间不自动刷新 By bu6030 2022/8/3 17:36  
3. 对基金股票按照code正序排序 By bu6030 2022/8/4 00:27  
4. 修改docker挂在db文件，解决每次重启数据清零的问题 By bu6030 2022/8/4 10:04  
5. 修改股票码去掉sh，sz后正序排序 By bu6030 2022/8/4 10:19  
6. 股票基金先按证券公司app排序，在按照code排序 Xuesong.Bu 2022/8/4 10:33  
7. 修改开盘前新浪基金返回估算净值为0展示出错 By bu6030 2022/8/5 9:55  
8. 新基金页面引入layui Xuesong.Bu 2022/8/5 17:58  
9. 修改新基金/股票页面 bu6030 2022/8/6 22:39  
10. 修改新基金股票汇总页面 bu6030 2022/8/7 00:35  
11. 统计当日盈亏 bu6030 2022/8/11 20:00  
12. 修改盈利汇总 Xuesong.Bu 2022/8/12 17:06  
13. 增加盈利统计手动统计以及删除当日统计 Xuesong.Bu 2022/8/15 13:40  
14. 增加字典表信息 Xuesong.Bu 2022/8/15 16:20  
15. 增加持仓占比 Xuesong.Bu 2022/8/15 17:17  
16. 所有百分比，排序问题修改 Xuesong.Bu 2022/8/16 13:27  
17. 增加作者 bu6030 2022/8/17 21:34  
18. layui新增字典页面 Xuesong.Bu 2022/8/18 11:03  
19. 新增盈利统计日期筛选 Xuesong.Bu 2022/8/19 17:27  
20. 增加当周当月当日盈利筛选，增加收益汇总，盈利红色，亏损绿色 Xuesong.Bu 2022/8/19 18:34  
21. 修改周日获取当周第一天最后一天错误 bu6030 2022/8/21 14:00  
22. 修改涨跌颜色 Xuesong.Bu 2022/8/23 15:00  
23. 修改颜色后串行，修改版本页面为Layui以及Bootstrap bu6030 2022/8/23 19:47  
24. 增加独立自动刷新js Xuesong.Bu 2022/8/29 00:55  
25. 增加layui过滤app，支持点击app只展示该app的股票、基金 Xuesong.Bu 2022/8/30 17:01  
26. 增加非交易时间从缓存取，减少接口调用 bu6030 2022/8/31 00:56  
27. 修改按月筛选day小于10时报错，day小于10前面补0，例如2022-09-02 Xuesong.Bu 2022/9/2 22:11  
28. 修改Bootstrap股票基金汇总中股票或基金当日盈利为0，不变色 Xuesong.Bu 2022/9/7 13:48
29. 股票新增默认1手，也就是100股 bu6030 2022/9/8 21:18
30. 去掉基金股票编码展示，优化页面ui Xuesong.Bu 2022/9/8 23:23
31. 股票/基金/字典数据的添加/修改页面修改弹窗为模态框modal Xuesong.Bu 2022/9/9 23:30
32. 修改layer关闭按钮不关闭问题 bu6030 2022/9/10 23:55
33. 添加基金去掉默认份额100份 bu6030 2022/9/10 23:02
34. layui去掉当日净值 Xuesong.Bu 2022/9/15 22:03
35. 增加买卖股票接口 Xuesong.Bu 2022/9/21 00:20
36. 卖出增加卖出收益，买卖均增加开盘价格 Xuesong.Bu 2022/9/21 17:39
37. 修改计算价格 Xuesong.Bu 2022/9/21 18:00
38. 处理股票清仓报错 bu6030 2022/9/21 21:31
39. Bootstrap版本增加股票买卖 Xuesong.Bu 2022/9/22 21:39
40. 增加买卖股票默认值，购买默认成本5元，卖出默认手续费0元（东方财富第二天才结算，咱们第二天手动更新成本即可） Xuesong.Bu 2022/9/22 22:03
41. 买卖后计算成本保留三位小数 Xuesong.Bu 2022/9/23 14:16
42. 修改十月份获取当月第一天错误 bu6030 2022/10/1 20:32
43. 修改定时刷新，bootstrap页面增加过滤隐藏展示股票功能 Xuesong.Bu 2022/10/28 16:18
44. 修改隐藏后，购买股票失败问题 Xuesong.Bu 2022/10/31 11:00
45. 去掉无用页面代码，修改重新渲染页面导致app丢失 Xuesong.Bu 2022/11/3 14:41
46. 修改与买卖页面增加展示股票/基金名称 Xuesong.Bu 2022/11/3 15:18
47. 修改Layui当日盈利排序显示错误 Xuesong.Bu 2022/11/4 14:12
48. 点击过买卖股票，添加股票/基金后，再点击添加股票/基金后展示上一次名字 Xuesong.Bu 2022/11/4 15:04
49. 初始化DDD模式代码结构 Xuesong.Bu 2022/11/21, 14:27
50. 增加每日邮件备份数据文件 Xuesong.Bu 2022/12/12, 16:43
51. 增加当日盈亏/合计盈亏比例，展示盈利亏损颜色 Xuesong.Bu 2022/12/14, 16:04
52. 盈利汇总倒序排列，合计放在最前面 Xuesong.Bu 2022/12/28, 09:27
53. 收益汇总，年底当周日期跨年导致统计当周盈利报错 Xuesong.Bu 2022/12/28, 09:44
54. 修改为SpringBoot3.0 Xuesong.Bu 2023/1/16, 23:05
55. 缓存没有失效问题，导致统计错误 Xuesong.Bu 2023/1/17, 21:15
56. 为chrome插件提供接口 Xuesong.Bu 2023/2/27, 22:50
57. 防止统计数据偶尔抽风没跑 Xuesong.Bu 2023/3/15, 23:06
58. 获取20天最高最低价格，增加唐安奇通道法监控程序 bu6030 2023/4/3 00:26
59. 增加月度/年度收益汇总 Xuesong.Bu 2023/4/10, 21:24
60. 增加唐安奇通道法计算股票历史数据，增加分红转股赠股 Xuesong.Bu 2023/4/10 22:44
61. 防止统计数据偶尔抽风没跑 Xuesong.Bu 2023/4/12, 15:27
62. 去掉没用的lib Xuesong.Bu 2023/4/12, 17:16
63. 修改添加可转债后，没有历史交易数据报错 Xuesong.Bu 2023/4/18, 16:14
64. 增加股票基金历史数据表 Xuesong.Bu 2023/4/20, 10:56
65. 增加股票基金历史数据查询接口 Xuesong.Bu 2023/4/21, 09:40
66. 获取股票历史，低于20天的股票不计算唐安奇通道法 Xuesong.Bu 2023/4/25, 17:34
67. 增加股票买卖历史接口 Xuesong.Bu 2023/4/27, 13:53
68. 增加通过code查询股票基金api Xuesong.Bu 2023/4/27, 14:46
69. 将上证指数，深成指数，创业板指数增加到头部展示 Xuesong.Bu 2023/5/12, 16:38
70. 优化头部展示的大盘指数展示效果 bu6030 2023/5/13, 00:01
71. 增加分时图（股票基金），日线图（股票），月线图（股票），周线图（股票） Xuesong.Bu 2023/5/28, 00:34
72. 股票列表支持添加场内ETF Xuesong.Bu 2023/6/1, 23:45
73. 场内ETF买卖计算成本四舍五入修改 bu6030 2023/6/8, 22:58
74. 增加年度月度收益汇总图表 Xuesong.Bu 2023/6/12, 23:17
75. 大盘指数点击显示走势图 Xuesong.Bu 2023/6/14, 11:34
76. 增加成本，重新精确计算收益率，涨跌幅 Xuesong.Bu 2023/6/25, 16:00
77. 增加交易日数据入库，每月只需要调用一次即可 Xuesong.Bu 2023/7/4, 17:03
78. 增加用户登录 Xuesong.Bu 2023/7/5, 13:50
79. 定时统计所有用户盈亏 Xuesong.Bu 2023/7/5, 16:03
80. 修改静态文件访问速度慢的问题 bu6030 2023/7/5, 21:10
81. 由于 SpringSecurity 导致 js 等文件不缓存，回到老版本 SpringBoot Xuesong.Bu 2023/7/6, 13:52
82. 老版本 mybatis 参数必须要有@Param Xuesong.Bu 2023/7/6, 15:04
83. 统计所有用户当日盈利时，计算问题修改 Xuesong.Bu 2023/7/6, 15:39
84. type 类型属于共用，去掉用户属性 Xuesong.Bu 2023/7/6, 16:14
85. 当用户没有持股时，不统计当日盈利 Xuesong.Bu 2023/7/6, 16:58
86. 增加通过名称搜索添加股票基金 Xuesong.Bu 2023/7/7, 14:35
87. 重新回到 SpringBoot 3.1.1，解决 WebSecurity.ignoring Xuesong.Bu 2023/7/11, 10:57
88. 增加定时线程池，如果不配置，会导致单线程阻塞执行所有定时 Xuesong.Bu 2023/7/11, 16:23
89. 更新 maven 中的包到新版本 Xuesong.Bu 2023/7/11, 17:41
90. 去掉无用代码 Xuesong.Bu 2023/7/14, 10:57
91. 增加登录页面本地代码 bu6030 2023/7/29, 23:22
92. 增加股票买卖记录以及股票/基金历史 2023/7/30, 00:31
93. 增加通过名称搜索股票基金 2023/8/1, 23:50
94. 增加基金日线/周线/月线图 2023/8/16, 22:43

# <span id="jump4">UI页面</span>
![image](https://user-images.githubusercontent.com/11482988/191644817-b8fd3dcf-e8aa-4582-84b0-afb2927d156d.png)  
股票页面Bootstrap风格  

![image](https://user-images.githubusercontent.com/11482988/191644906-f475dd4c-2a19-4bfb-a597-2d0805f07a18.png)  
股票页面Layui风格  

![image](https://user-images.githubusercontent.com/11482988/191645032-f6877706-45c7-4dba-8dd2-95a76e9c6d1b.png)  
买入/卖出股票页面  

![image](https://user-images.githubusercontent.com/11482988/189321022-9f6b4889-4730-4b29-aed0-a63692496693.png)  
添加/修改股票页面  

![image](https://user-images.githubusercontent.com/11482988/187102557-eceb7eb4-bb02-46fc-88e7-e826b36dab94.png)  
基金页面Bootstrap风格  

![image](https://user-images.githubusercontent.com/11482988/187102572-6cca4390-d854-4bab-a4b0-a8dc3e6113fb.png)  
基金页面Layui风格  

![image](https://user-images.githubusercontent.com/11482988/187102586-7532e589-0548-4158-aa55-3ff5e91f678e.png)  
股票基金汇总Bootstrap风格  

![image](https://user-images.githubusercontent.com/11482988/187102599-fc19736d-808e-4ac6-8f3d-044c7b846e27.png)  
股票基金汇总Layui风格  

![image](https://user-images.githubusercontent.com/11482988/187102638-8cbf30d5-a500-4c76-8bce-6313a7b916df.png)  
收益汇总Bootstrap风格  

![image](https://user-images.githubusercontent.com/11482988/187102651-fa629e8c-0524-4c6a-aa56-9091f9f0ed50.png)  
收益汇总Layui风格  

![image](https://user-images.githubusercontent.com/11482988/187102663-91f5d6bc-15fa-403f-a674-3561b531aaa8.png)  
字典数据页面Bootstrap风格  

![image](https://user-images.githubusercontent.com/11482988/187102675-6c9b9fce-5f26-4388-8ad8-1cba51ec98a8.png)  
字典数据页面Layui风格  

![image](https://user-images.githubusercontent.com/11482988/189321234-e7416c96-5cb4-4b1a-af9d-a593dc0c0b6d.png)  
添加/修改字典数据  

## <span id="jump5">部署</span>
### 1. 本机Docker环境一键部署
1. 本地package出jar包
2. 修改localDeployment.sh文件中的本地sqllite文件绝对路径位置，也就是项目中的stock-and-fund.db文件的绝对路径
执行项目根目录下localDeployment.sh
./localDeployment.sh
3. 本机访问http://localhost:8080

### 2. 本机Idea启动
1. 修改Run/Debug Configurations->Spring Boot中增加环境变量，sqllite文件绝对路径位置，也就是项目中的stock-and-fund.db文件的绝对路径
sqllite.db.file=你的项目路径/stock-and-fund.db
2. 本机通过SpringBoot start类直接run main方法启动
3. 本机访问http://localhost:8080

### 3. 下载release jar包
1. 打开链接 https://github.com/bu6030/stock-and-fund/releases
2. 下载最新Release中的jar包以及db文件，例如stock-and-fund-1.6.0-SNAPSHOT.jar和stock-and-fund.db
3. 本机执行以下命令
```
java -jar -Dsqllite.db.file=你的项目路径/stock-and-fund.db stock-and-fund-1.6.0-SNAPSHOT.jar
```
4. 本机访问http://localhost:8080

## <span id="jump6">初始化表结构</span>
### 盈利汇总数据
```
CREATE TABLE DEPOSIT (
  DATE TEXT(10) NOT NULL,
  FUND_DAY_INCOME CHAR(20) NOT NULL,
  STOCK_DAY_INCOME CHAR(20) NOT NULL,
  DAY_INCOME CHAR(20) NOT NULL,
  FUND_MARKET_VALUE CHAR(20) NOT NULL,
  STOCK_MARKET_VALUE CHAR(20) NOT NULL,
  TOTAL_MARKET_VALUE CHAR(20) NOT NULL,
  USERNAME varchar(50) NOT NULL
);
CREATE INDEX IDX_DEPOSIT_USERNAME ON PARAM(USERNAME);
```
### 基金数据
```
CREATE TABLE FUND (
  CODE TEXT(10) PRIMARY KEY NOT NULL,
  NAME TEXT(100),
  COST_PRICE CHAR(20) NOT NULL,
  BONDS CHAR(30) NOT NULL,
  APP CHAR(10) NOT NULL,
  HIDE INTEGER,
  USERNAME varchar(50) NOT NULL
);
```
### 基金历史数据
```
CREATE TABLE FUND_HIS (
  CODE TEXT(10) NOT NULL,
  NAME TEXT(100),
  COST_PRICE CHAR(20) NOT NULL,
  BONDS CHAR(30) NOT NULL,
  APP CHAR(10) NOT NULL,
  HIDE INTEGER,
  CREATE_DATE DATETIME,
  USERNAME varchar(50) NOT NULL
);
```
### 股票数据
```
CREATE TABLE STOCK (
  CODE TEXT(10) PRIMARY KEY NOT NULL,
  NAME TEXT(100),
  COST_PRICE CHAR(20) NOT NULL,
  BONDS INT NOT NULL,
  APP CHAR(10) NOT NULL,
  HIDE INTEGER,
  USERNAME varchar(50) NOT NULL
);
```
### 股票历史数据
```
CREATE TABLE STOCK_HIS (
  CODE TEXT(10) NOT NULL,
  NAME TEXT(100),
  COST_PRICE CHAR(20) NOT NULL,
  BONDS INT NOT NULL,
  APP CHAR(10) NOT NULL,
  HIDE INTEGER,
  CREATE_DATE DATETIME,
  USERNAME varchar(50) NOT NULL
);
```
### 字典数据
```
CREATE TABLE PARAM (
  TYPE TEXT(10) NOT NULL,
  CODE CHAR(20) NOT NULL,
  NAME INT NOT NULL,
  USERNAME varchar(50) NOT NULL,
  PRIMARY KEY (`TYPE`,`CODE`)
);
CREATE INDEX IDX_PARAM_TYPE ON PARAM(TYPE);
```
### 买卖数据
```
CREATE TABLE BUY_OR_SELL (
  DATE TEXT(10) NOT NULL,
  CODE TEXT(10) NOT NULL,
  TYPE CHAR(2) NOT NULL,
  PRICE CHAR(20) NOT NULL,
  COST CHAR(20),
  BONDS CHAR(20) NOT NULL,
  INCOME CHAR(20) NOT NULL,
  OPENPRICE CHAR(20) NOT NULL,
  USERNAME varchar(50) NOT NULL
);
```
### 交易日数据
```
CREATE TABLE OPEN_PERSISTENT_MONTH (
  MONTH TEXT(10) PRIMARY KEY NOT NULL,
  DATA TEXT(512) NOT NULL
);
```
### 登录用户数据
```
CREATE TABLE users (
  username varchar(50) NOT NULL,
  password varchar(100) NOT NULL,
  enabled tinyint(1) NOT NULL,
  PRIMARY KEY (username)
);
```
### 登录用户数据
```
CREATE TABLE authorities (
  username varchar(64) NOT NULL,
  authority varchar(64) NOT NULL,
  CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username)
);
```