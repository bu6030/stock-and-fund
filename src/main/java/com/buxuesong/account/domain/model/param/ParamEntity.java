package com.buxuesong.account.domain.model.param;

import com.buxuesong.account.apis.model.request.ParamRequest;
import com.buxuesong.account.infrastructure.persistent.po.ParamPO;
import com.buxuesong.account.infrastructure.persistent.repository.ParamMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ParamEntity {
    @Autowired
    private ParamMapper paramMapper;

    public List<ParamPO> getParamList() {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        List<ParamPO> list = paramMapper.findAllParam(username);
        log.info("Get Parameter list : {}", list);
        return list;
    }

    public List<ParamPO> getParamList(String type) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        List<ParamPO> list = paramMapper.findParamByType(type, username);
        log.info("Get paramEntity list : {}", list);
        return list;
    }

    public void saveParam(ParamRequest paramRequest) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        ParamPO paramPO = ParamPO.builder().code(paramRequest.getCode()).name(paramRequest.getName()).type(paramRequest.getType()).build();
        ParamPO paramPOOld = paramMapper.findParamByTypeAndCode(paramPO, username);
        if (paramPOOld != null) {
            paramMapper.update(paramPO, username);
        } else {
            paramMapper.save(paramPO, username);
        }
    }

    public void deleteParam(ParamRequest paramRequest) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        paramMapper
            .delete(ParamPO.builder().code(paramRequest.getCode()).name(paramRequest.getName()).type(paramRequest.getType()).build(),
                username);
    }
}
