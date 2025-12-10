package com.easylive.aspect;


import com.easylive.annotation.RecordUserMessage;
import com.easylive.component.RedisComponent;
import com.easylive.entity.constants.Constants;
import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.enums.MessageTypeEnum;
import com.easylive.enums.UserActionTypeEnum;
import com.easylive.exception.BusinessException;
import com.easylive.redis.RedisUtils;
import com.easylive.service.UserMessageService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Component
@Aspect
@Slf4j
public class UserMessageOperactionAspect {
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private UserMessageService userMessageService;
    private static final String PARAMETER_VIDEO_ID = "videoId";
    private static final String PARAMETER_ACTION_TYPE = "actionType";
    private static final String PARAMETER_REPLY_COMMENTID = "replyCommentId";
    private static final String PARAMETER_AUDIT_REJECT_REASON = "reason";
    private static final String PARAMETERS_CONTENT = "content";
    @Autowired
    private RedisUtils redisUtils;

    @Around("@annotation(com.easylive.annotation.RecordUserMessage)")
    public ResponseVO interceptorDo(ProceedingJoinPoint point) throws Throwable {
        ResponseVO responseVO = (ResponseVO) point.proceed();
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        RecordUserMessage recordUserMessage = method.getAnnotation(RecordUserMessage.class);
        if (recordUserMessage != null) {
            saveMessage(recordUserMessage, point.getArgs(), method.getParameters());
        }
        return responseVO;
    }

    private void saveMessage(RecordUserMessage recordUserMessage, Object[] arguments, Parameter[] parameters) {
        String videoId = null;
        Integer actionType = null;
        Integer replyCommentId = null;
        String content = null;

        for (int i = 0; i < parameters.length; i++) {
            if (PARAMETER_VIDEO_ID.equals(parameters[i].getName())) {
                videoId = (String) arguments[i];
            } else if (PARAMETER_ACTION_TYPE.equals(parameters[i].getName())) {
                actionType = (Integer) arguments[i];
            } else if (PARAMETER_REPLY_COMMENTID.equals(parameters[i].getName())) {
                replyCommentId = (Integer) arguments[i];
            } else if (PARAMETERS_CONTENT.equals(parameters[i].getName())) {
                content = (String) arguments[i];
            } else if (PARAMETER_AUDIT_REJECT_REASON.equals(parameters[i].getName())) {
                content = (String) arguments[i];
            }
        }

        MessageTypeEnum messageTypeEnum = recordUserMessage.messageType();
        if(UserActionTypeEnum.VIDEO_COLLECT.getType().equals(actionType)){
            messageTypeEnum = MessageTypeEnum.COLLECTION;
        }

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();

        userMessageService.saveUserMessage(videoId,tokenUserInfoDto==null?null:tokenUserInfoDto.getUserId(),messageTypeEnum,content,replyCommentId);

    }

    private TokenUserInfoDto getTokenUserInfoDto(){
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(Constants.TOKEN_WEB);
        return redisComponent.getTokenInfo(token);
    }
}
