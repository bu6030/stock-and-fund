package com.buxuesong.account.apis.model.request;

public class AdviceRequest {
    private Long id;
    private String adviceContent;
    private String date;
    private String adviceDevelopVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdviceContent() {
        return adviceContent;
    }

    public void setAdviceContent(String adviceContent) {
        this.adviceContent = adviceContent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAdviceDevelopVersion() {
        return adviceDevelopVersion;
    }

    public void setAdviceDevelopVersion(String adviceDevelopVersion) {
        this.adviceDevelopVersion = adviceDevelopVersion;
    }

    @Override
    public String toString() {
        return "AdviceRequest{" +
                "id=" + id +
                ", adviceContent='" + adviceContent + '\'' +
                ", date='" + date + '\'' +
                ", adviceDevelopVersion='" + adviceDevelopVersion + '\'' +
                '}';
    }
}
