package com.buxuesong.account.infrastructure.adapter.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundSearchResponse {
    private int ErrCode;
    private String ErrMsg;
    private List<FundData> Datas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FundData {
        private String HIGHTLIGHT;
        private String _id;
        private String CODE;
        private String NAME;
        private String JP;
        private int CATEGORY;
        private String CATEGORYDESC;
        private String STOCKMARKET;
        private String BACKCODE;
        private int MatchCount;
        private FundBaseInfo FundBaseInfo;
        private String StockHolder;
        private List<Object> ZTJJInfo;
        private double SEARCHWEIGHT;
        private String NEWTEXCH;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FundBaseInfo {
        private String _id;
        private double DWJZ;
        private String FCODE;
        private String FSRQ;
        private String FTYPE;
        private String FUNDTYPE;
        private String ISBUY;
        private String JJGS;
        private double JJGSBID;
        private String JJGSID;
        private String JJJL;
        private String JJJLID;
        private double MINSG;
        private String OTHERNAME;
        private String SHORTNAME;
        private String RSFUNDTYPE;
        private String NAVURL;
    }
}
