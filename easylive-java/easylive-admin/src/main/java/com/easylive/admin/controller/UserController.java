package com.easylive.admin.controller;

import com.easylive.entity.query.InfoQuery;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.InfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
public class UserController extends ABaseController {
    @Resource
    private InfoService infoService;

    @RequestMapping("/loadUser")
    public ResponseVO loadUser(InfoQuery userInfoQuery) {
        userInfoQuery.setOrderBy("join_time desc");
        return getSuccessResponseVO(infoService.findListByParam(userInfoQuery));
    }

    @RequestMapping("/changeStatus")
    public ResponseVO changeStatus(String userId, Integer status) {
        infoService.changeUserStatus(userId,status);
        return getSuccessResponseVO(null);
    }
}
