package com.easylive.service.impl;

import com.easylive.entity.dto.UserMessageCountDto;
import com.easylive.entity.dto.UserMessageExtendDto;
import com.easylive.entity.po.UserMessage;
import com.easylive.entity.po.VideoComment;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.po.VideoInfoPost;
import com.easylive.entity.query.*;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.MessageReadTypeEnum;
import com.easylive.enums.MessageTypeEnum;
import com.easylive.enums.PageSize;
import com.easylive.mappers.UserMessageMapper;
import com.easylive.mappers.VideoCommentMapper;
import com.easylive.mappers.VideoInfoMapper;
import com.easylive.mappers.VideoInfoPostMapper;
import com.easylive.service.UserMessageService;
import com.easylive.utils.JsonUtils;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNullFormatVisitor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Description用户信息表Service
 * @date:2025/10/29
 * @author:magnico
 */
@Service("userMessageService")
public class UserMessageServiceImpl implements UserMessageService {

	@Resource
	private UserMessageMapper<UserMessage, UserMessageQuery> userMessageMapper;
	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;
	@Resource
	private VideoCommentMapper<VideoComment, VideoCommentQuery> videoCommentMapper;
	@Resource
	private VideoInfoPostMapper<VideoInfoPost, VideoInfoPostQuery> videoInfoPostMapper;


	/**
	 * 根据条件查询列表
	 */
	public List<UserMessage> findListByParam(UserMessageQuery query) {
		return this.userMessageMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserMessageQuery query) {
		return this.userMessageMapper.selectCount(query);
	}

	@Override
	public Integer updateByParam(UserMessage bean, UserMessageQuery query) {
		return this.userMessageMapper.updateByParam(bean, query);
	}

	@Override
	public Integer deleteByParam(UserMessageQuery query) {
		return this.userMessageMapper.deleteByParam(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserMessage> findListByPage(UserMessageQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserMessage> list = this.findListByParam(query);
		PaginationResultVO<UserMessage> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(),list);		
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserMessage bean) {
		return this.userMessageMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserMessage> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.userMessageMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserMessage> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.userMessageMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据MessageId查询
	 */
	public UserMessage getUserMessageByMessageId(Integer messageId) {
		return this.userMessageMapper.selectByMessageId(messageId);
	}

	/**
	 * 根据MessageId更新
	 */
	public Integer updateUserMessageByMessageId(UserMessage bean, Integer messageId) {
		return this.userMessageMapper.updateByMessageId(bean,messageId);
	}

	/**
	 * 根据MessageId删除
	 */
	public Integer deleteUserMessageByMessageId(Integer messageId) {
		return this.userMessageMapper.deleteByMessageId(messageId);
	}

	@Override
	@Async
	public void saveUserMessage(String videoId, String sendUserId, MessageTypeEnum messageTypeEnum, String content, Integer replyCommentId) {
		VideoInfo videoInfo = videoInfoMapper.selectByVideoId(videoId);
		if (videoInfo == null) {
			return;
		}
		UserMessageExtendDto extendDto = new UserMessageExtendDto();
		extendDto.setMessageContent(content);

		String userId = videoInfo.getUserId();

		//收藏，点赞，已经记录的，不再记录
		if(ArrayUtils.contains(new Integer[]{MessageTypeEnum.LIKE.getType(), MessageTypeEnum.COLLECTION.getType()},messageTypeEnum.getType())){
			UserMessageQuery userMessageQuery = new UserMessageQuery();
			userMessageQuery.setUserId(userId);
			userMessageQuery.setVideoId(videoId);
			userMessageQuery.setMessageType(messageTypeEnum.getType());
			Integer count = userMessageMapper.selectCount(userMessageQuery);

			if(count > 0){
				return;
			}

		}

		UserMessage userMessage = new UserMessage();
		userMessage.setUserId(userId);
		userMessage.setVideoId(videoId);
		userMessage.setReadType(MessageReadTypeEnum.NO_READ.getType());
		userMessage.setCreateTime(new Date());
		userMessage.setMessageType(messageTypeEnum.getType());
		userMessage.setSendUserId(sendUserId);

		//评论特殊处理
		if(replyCommentId != null){
			VideoComment comment = videoCommentMapper.selectByCommentId(replyCommentId);
			if(null!=comment){
				userId = comment.getUserId();
				extendDto.setMessageContentReply(comment.getContent());
			}
		}
		if(userId.equals(sendUserId)){
			return;
		}

		//系统消息特殊处理

		if(MessageTypeEnum.SYS==messageTypeEnum){
			VideoInfoPost videoInfoPost = videoInfoPostMapper.selectByVideoId(videoId);
			extendDto.setAuditStatus(videoInfoPost.getStatus());
		}
		userMessage.setUserId(userId);
		userMessage.setExtendJson(JsonUtils.convertObj2Json(extendDto));
		this.userMessageMapper.insert(userMessage);
	}

	@Override
	public List<UserMessageCountDto> getMessageTypeNoReadCount(String userId) {
		return this.userMessageMapper.getMessageTypeNoReadCount(userId);
	}
}