package com.buxuesong.account.apis.model.request;

public class ParamRequest {
    private String id;
    private String type;
    private String code;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
        return "ParamRequest{" +
            "id='" + id + '\'' +
            ", type='" + type + '\'' +
            ", code='" + code + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
