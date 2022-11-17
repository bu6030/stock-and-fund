package com.buxuesong.account.domain.model.param;

import com.buxuesong.account.apis.model.request.ParamRequest;
import com.buxuesong.account.infrastructure.persistent.entity.ParamPO;
import com.buxuesong.account.infrastructure.persistent.repository.ParamMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ParamEntity {
    @Autowired
    private ParamMapper paramMapper;

    public List<ParamPO> getParamList() {
        List<ParamPO> list = paramMapper.findAllParam();
        log.info("Get Parameter list : {}", list);
        return list;
    }

    public List<ParamPO> getParamList(String type) {
        List<ParamPO> list = paramMapper.findParamByType(type);
        log.info("Get paramEntity list : {}", list);
        return list;
    }

    public void saveParam(ParamRequest paramRequest) {
        ParamPO paramPO = ParamPO.builder().code(paramRequest.getCode()).name(paramRequest.getName()).type(paramRequest.getType()).build();
        ParamPO paramPOOld = paramMapper.findParamByTypeAndCode(paramPO);
        if (paramPOOld != null) {
            paramMapper.update(paramPO);
        } else {
            paramMapper.save(paramPO);
        }
    }

    public void deleteParam(ParamRequest paramRequest) {
        paramMapper.delete(ParamPO.builder().code(paramRequest.getCode()).name(paramRequest.getName()).type(paramRequest.getType()).build());
    }
}
