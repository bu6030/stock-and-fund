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
public class FundNetDiagramResponse {

    private List<DataItem> Datas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataItem {
        private String FSRQ;
        private String DWJZ;
        private String JZZZL;
        private String LJJZ;
        private String NAVTYPE;
        private String RATE;
        private String FHFCZ;
        private String FHFCBZ;
        private String Remarks;
    }
}
