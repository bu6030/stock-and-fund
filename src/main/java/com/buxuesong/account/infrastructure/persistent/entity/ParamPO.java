package com.buxuesong.account.infrastructure.persistent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamPO {
    private String type;
    private String code;
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Param{" +
            "type='" + type + '\'' +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
