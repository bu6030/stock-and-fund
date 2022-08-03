package com.buxuesong.account.model;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class FundBean {
    @SerializedName("fundcode")
    private String fundCode;
    @SerializedName("name")
    private String fundName;
    private String jzrq;// 净值日期
    private String dwjz;// 当日净值
    private String gsz; // 估算净值
    private String gszzl;// 估算涨跌百分比 即-0.42%
    private String gztime;// gztime估值时间

    private String costPrise;// 持仓成本价
    private String bonds;// 持有份额
    private String app;// 支付宝/东方财富/东方证券
    private String incomePercent;// 收益率
    private String income;// 收益

    public FundBean() {
    }

    public FundBean(String fundCode) {
        if (StringUtils.isNotBlank(fundCode)) {
            String[] codeStr = fundCode.split(",");
            if (codeStr.length > 2) {
                this.fundCode = codeStr[0];
                this.costPrise = codeStr[1];
                this.bonds = codeStr[2];
            } else {
                this.fundCode = codeStr[0];
                this.costPrise = "--";
                this.bonds = "--";
            }
        } else {
            this.fundCode = fundCode;
        }
        this.fundName = "--";
    }

    public static void loadFund(FundBean fund, Map<String, String[]> codeMap) {
        String code = fund.getFundCode();
        if (codeMap.containsKey(code)) {
            String[] codeStr = codeMap.get(code);
            if (codeStr.length > 3) {
                fund.setCostPrise(codeStr[1]);
                fund.setBonds(codeStr[2]);
                fund.setApp(codeStr[3]);
            } else {
                fund.setCostPrise(codeStr[1]);
                fund.setBonds(codeStr[2]);
            }
        }
    }

    public static FundBean loadFundFromSina(String code, String fundStr) {
        fundStr = fundStr.replaceAll("var hq_str_sz" + code + "=", "")
            .replaceAll("var hq_str_f_" + code + "=", "").replaceAll("\"", "");
        String[] lines = fundStr.split("\n");
        FundBean fundBean = new FundBean();
        String[] fundInfoArr = lines[0].split(",");
        String[] fundJingZhiArr = lines[1].split(",");
        fundBean.setFundCode(code);
        fundBean.setFundName(fundJingZhiArr[0]);
        fundBean.setJzrq(fundJingZhiArr[4]);
        fundBean.setDwjz(fundInfoArr[2]);
        fundBean.setGsz(fundInfoArr[3]);
        BigDecimal gsz = new BigDecimal(fundInfoArr[3]);
        BigDecimal dwjz = new BigDecimal(fundInfoArr[2]);
        BigDecimal gszzl = gsz.subtract(dwjz).divide(gsz, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2);
        fundBean.setGszzl(gszzl.toString());
        return fundBean;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getJzrq() {
        return jzrq;
    }

    public void setJzrq(String jzrq) {
        this.jzrq = jzrq;
    }

    public String getDwjz() {
        return dwjz;
    }

    public void setDwjz(String dwjz) {
        this.dwjz = dwjz;
    }

    public String getGsz() {
        return gsz;
    }

    public void setGsz(String gsz) {
        this.gsz = gsz;
    }

    public String getGszzl() {
        return gszzl;
    }

    public void setGszzl(String gszzl) {
        this.gszzl = gszzl;
    }

    public String getGztime() {
        return gztime;
    }

    public void setGztime(String gztime) {
        this.gztime = gztime;
    }

    public String getCostPrise() {
        return costPrise;
    }

    public void setCostPrise(String costPrise) {
        this.costPrise = costPrise;
    }

    public String getBonds() {
        return bonds;
    }

    public void setBonds(String bonds) {
        this.bonds = bonds;
    }

    public String getIncomePercent() {
        return incomePercent;
    }

    public void setIncomePercent(String incomePercent) {
        this.incomePercent = incomePercent;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FundBean fundBean = (FundBean) o;
        return Objects.equals(fundCode, fundBean.fundCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fundCode);
    }

    @Override
    public String toString() {
        return "FundBean{" +
            "fundCode='" + fundCode + '\'' +
            ", fundName='" + fundName + '\'' +
            ", jzrq='" + jzrq + '\'' +
            ", dwjz='" + dwjz + '\'' +
            ", gsz='" + gsz + '\'' +
            ", gszzl='" + gszzl + '\'' +
            ", gztime='" + gztime + '\'' +
            ", costPrise='" + costPrise + '\'' +
            ", bonds='" + bonds + '\'' +
            ", incomePercent='" + incomePercent + '\'' +
            ", income='" + income + '\'' +
            ", app='" + app + '\'' +
            '}';
    }
}
