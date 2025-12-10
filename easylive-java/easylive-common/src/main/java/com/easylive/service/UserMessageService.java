package com.easylive.service;

import com.easylive.entity.dto.UserMessageCountDto;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.po.UserMessage;
import com.easylive.entity.query.UserMessageQuery;
import com.easylive.enums.MessageTypeEnum;

import java.util.List;
/**
 * @Description用户信息表Service
 * @date:2025/10/29
 * @author:magnico
 */
public interface UserMessageService{

	/**
	 * 根据条件查询列表
	 */
	List<UserMessage> findListByParam(UserMessageQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserMessageQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserMessage> findListByPage(UserMessageQuery query);

	/**
	 * 新增
	 */
	Integer add(UserMessage bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserMessage> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserMessage> listBean);

	/**
	 * 根据MessageId查询
	 */
	UserMessage getUserMessageByMessageId(Integer messageId);

	/**
	 * 根据MessageId更新
	 */
	Integer updateUserMessageByMessageId(UserMessage bean, Integer messageId);

	Integer updateByParam(UserMessage bean, UserMessageQuery query);

	Integer deleteByParam(UserMessageQuery query);

	/**
	 * 根据MessageId删除
	 */
	Integer deleteUserMessageByMessageId(Integer messageId);

	void saveUserMessage(String videoId, String sendUserId, MessageTypeEnum messageTypeEnum,String content,Integer replyCommentId);

	List<UserMessageCountDto> getMessageTypeNoReadCount(String userId);
}