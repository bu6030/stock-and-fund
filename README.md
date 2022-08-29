# 股基神器
自己写着玩的一个股票基金SpringBoot小项目，我给它命名股基神器  
我自己有多个APP购买股票基金的情况，每次还要到多个APP查看，很费劲  
因此自己写了个汇总的，只支持中国股票基金，录入基金股票编码，购买成本，持仓数量来添加股票基金，便可以实时查看价格以及盈亏
可实时查看股票基金实时价格，周一至周五交易时间定时刷新  
单独查看股票或者基金实时价格，以及汇总股票基金实时价格
添加删除股票基金  
查看历史盈亏记录，可按周月年筛选统计  
周一至周五下午3点30自动统计当日盈亏  
手动添加股票基金APP，例如东方财富，支付宝，微信等  
多个页面UI，Bootstrap以及Layui样式  
具体内容们可以查看我的帖子：<https://zhuanlan.zhihu.com/p/557316975>  

![image](https://user-images.githubusercontent.com/11482988/187102488-be39a81a-bc0a-46a8-a314-af22bd5c6cdf.png)  
<center>股票页面Bootstrap风格</center>  
![image](https://user-images.githubusercontent.com/11482988/187102524-749f81ed-582f-4ff2-b677-5faf4e66a238.png)  
<center>股票页面Layui风格</center>  
![image](https://user-images.githubusercontent.com/11482988/187102545-3205da6e-578e-400d-8fa5-f65590ad06d5.png)  
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
![image](https://user-images.githubusercontent.com/11482988/187102693-1cbdb686-0feb-43f8-ad79-67c409aaff2d.png)  
<center>添加/修改字典数据</center>  

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

# 初始化表数据
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
