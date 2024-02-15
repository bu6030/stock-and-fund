package com.buxuesong.account.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvicePO {
    private Long id;
    private String date;
    private String adviceContent;
    private String adviceDevelopVersion;
}
