# 股基神器
自己研究写的一个股票基金SpringBoot小项目，我给它命名股基神器  
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
具体内容们可以查看我的帖子：<https://zhuanlan.zhihu.com/p/557316975>  
# 修改历史
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
# UI页面
![image](https://user-images.githubusercontent.com/11482988/191644817-b8fd3dcf-e8aa-4582-84b0-afb2927d156d.png) 
<center>股票页面Bootstrap风格</center>  

![image](https://user-images.githubusercontent.com/11482988/191644906-f475dd4c-2a19-4bfb-a597-2d0805f07a18.png)
<center>股票页面Layui风格</center>  

![image](https://user-images.githubusercontent.com/11482988/191645032-f6877706-45c7-4dba-8dd2-95a76e9c6d1b.png)
<center>买入/卖出股票页面</center>  

![image](https://user-images.githubusercontent.com/11482988/189321022-9f6b4889-4730-4b29-aed0-a63692496693.png)  
<center>添加/修改股票页面</center>  

![image](https://user-images.githubusercontent.com/11482988/187102557-eceb7eb4-bb02-46fc-88e7-e826b36dab94.png)  
<center>基金页面Bootstrap风格</center>  

![image](https://user-images.githubusercontent.com/11482988/187102572-6cca4390-d854-4bab-a4b0-a8dc3e6113fb.png)  
<center>基金页面Layui风格</center>  

![image](https://user-images.githubusercontent.com/11482988/187102586-7532e589-0548-4158-aa55-3ff5e91f678e.png)  
<center>股票基金汇总Bootstrap风格</center>  

![image](https://user-images.githubusercontent.com/11482988/187102599-fc19736d-808e-4ac6-8f3d-044c7b846e27.png)
<center>股票基金汇总Layui风格</center>  

![image](https://user-images.githubusercontent.com/11482988/187102638-8cbf30d5-a500-4c76-8bce-6313a7b916df.png)  
<center>收益汇总Bootstrap风格</center>  

![image](https://user-images.githubusercontent.com/11482988/187102651-fa629e8c-0524-4c6a-aa56-9091f9f0ed50.png)  
<center>收益汇总Layui风格</center>  

![image](https://user-images.githubusercontent.com/11482988/187102663-91f5d6bc-15fa-403f-a674-3561b531aaa8.png)  
<center>字典数据页面Bootstrap风格</center>  

![image](https://user-images.githubusercontent.com/11482988/187102675-6c9b9fce-5f26-4388-8ad8-1cba51ec98a8.png)  
<center>字典数据页面Layui风格</center>  

![image](https://user-images.githubusercontent.com/11482988/189321234-e7416c96-5cb4-4b1a-af9d-a593dc0c0b6d.png)  
<center>添加/修改字典数据</center>  

# 部署
## 本机Docker环境一键部署
1. 本地package出jar包
2. 修改localDeployment.sh文件中的本地sqllite文件绝对路径位置，也就是项目中的stock-and-fund.db文件的绝对路径
执行项目根目录下localDeployment.sh
./localDeployment.sh
3. 本机访问http://localhost:8080

## 本机Idea启动
1. 修改Run/Debug Configurations->Spring Boot中增加环境变量，sqllite文件绝对路径位置，也就是项目中的stock-and-fund.db文件的绝对路径
sqllite.db.file=/XXXX/XXXX/stock-and-fund.db
2. 本机通过SpringBoot start类直接run main方法启动
3. 本机访问http://localhost:8080

# 初始化表结构
## 盈利汇总数据
```
CREATE TABLE DEPOSIT (
  DATE TEXT(10) PRIMARY KEY NOT NULL,
  FUND_DAY_INCOME CHAR(20) NOT NULL,
  STOCK_DAY_INCOME CHAR(20) NOT NULL,
  DAY_INCOME CHAR(20) NOT NULL,
  FUND_MARKET_VALUE CHAR(20) NOT NULL,
  STOCK_MARKET_VALUE CHAR(20) NOT NULL,
  TOTAL_MARKET_VALUE CHAR(20) NOT NULL
);
```
## 基金数据
```
CREATE TABLE FUND (
  CODE TEXT(10) PRIMARY KEY NOT NULL,
  COST_PRICE CHAR(20) NOT NULL,
  BOUNDS CHAR(30) NOT NULL,
  APP CHAR(10) NOT NULL
);
```
## 股票数据
```
CREATE TABLE STOCK (
  CODE TEXT(10) PRIMARY KEY NOT NULL,
  COST_PRICE CHAR(20) NOT NULL,
  BOUNDS INT NOT NULL,
  APP CHAR(10) NOT NULL
);
```
## 字典数据
```
CREATE TABLE PARAM (
  TYPE TEXT(10) NOT NULL,
  CODE CHAR(20) NOT NULL,
  NAME INT NOT NULL,
  PRIMARY KEY (`TYPE`,`CODE`)
);
CREATE INDEX IDX_PARAM_TYPE ON PARAM(TYPE);
```
## 买卖数据
```
CREATE TABLE BUY_OR_SELL (
  DATE TEXT(10) NOT NULL,
  CODE TEXT(10) NOT NULL,
  TYPE CHAR(2) NOT NULL,
  PRICE CHAR(20) NOT NULL,
  COST CHAR(20),
  BONDS CHAR(20) NOT NULL,
  INCOME CHAR(20) NOT NULL,
  OPENPRICE CHAR(20) NOT NULL
);
```
