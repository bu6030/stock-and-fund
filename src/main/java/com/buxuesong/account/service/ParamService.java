package com.buxuesong.account.service;

import com.buxuesong.account.persist.entity.Parameter;

import java.util.List;

public interface ParamService {
    List<Parameter> getParamList();
    List<Parameter> getParamList(String type);
    Parameter getParamByTypeAndCode(Parameter parameter);
    void saveParam(Parameter parameter);
    void deleteParam(Parameter parameter);
}
