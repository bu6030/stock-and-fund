package com.buxuesong.account.service;

import com.buxuesong.account.persist.dao.ParamMapper;
import com.buxuesong.account.persist.entity.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ParamServiceImpl implements ParamService {

    @Autowired
    private ParamMapper paramMapper;

    @Override
    public List<Parameter> getParamList() {
        List<Parameter> list = paramMapper.findAllParam();
        log.info("Get deposit list : {}", list);
        return list;
    }

    @Override
    public List<Parameter> getParamList(String type) {
        List<Parameter> list = paramMapper.findParamByType(type);
        log.info("Get deposit list : {}", list);
        return list;
    }

    @Override
    public Parameter getParamByTypeAndCode(Parameter parameter) {
        return paramMapper.findParamByTypeAndCode(parameter);
    }

    @Override
    public void saveParam(Parameter parameter) {
        Parameter prameterOld = paramMapper.findParamByTypeAndCode(parameter);
        if (prameterOld != null) {
            paramMapper.update(parameter);
        } else {
            paramMapper.save(parameter);
        }
    }

    @Override
    public void deleteParam(Parameter parameter) {
        paramMapper.delete(parameter);
    }
}
