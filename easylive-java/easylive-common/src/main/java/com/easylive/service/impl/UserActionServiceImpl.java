package com.easylive.service.impl;

import com.easylive.component.EsSearchComponent;
import com.easylive.entity.constants.Constants;
import com.easylive.entity.po.Info;
import com.easylive.entity.po.UserAction;
import com.easylive.entity.po.VideoComment;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.*;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.PageSize;
import com.easylive.enums.ResponseCodeEnum;
import com.easylive.enums.SearchOrderTypeEnum;
import com.easylive.enums.UserActionTypeEnum;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.InfoMapper;
import com.easylive.mappers.UserActionMapper;
import com.easylive.mappers.VideoCommentMapper;
import com.easylive.mappers.VideoInfoMapper;
import com.easylive.service.UserActionService;
import com.fasterxml.jackson.databind.ser.std.StdArraySerializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description用户行为 点赞 评论Service
 * @date:2025/10/19
 * @author:magnico
 */
@Service("userActionService")
public class UserActionServiceImpl implements UserActionService {

	@Resource
	private UserActionMapper<UserAction, UserActionQuery> userActionMapper;

	@Resource
	private VideoCommentMapper<VideoComment, VideoCommentQuery> videoCommentMapper ;

	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

    @Resource
	private InfoMapper<Info,InfoQuery> infoMapper;
    @Autowired
    private EsSearchComponent esSearchComponent;

	/**
	 * 根据条件查询列表
	 */
	public List<UserAction> findListByParam(UserActionQuery query) {
		return this.userActionMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserActionQuery query) {
		return this.userActionMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserAction> findListByPage(UserActionQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserAction> list = this.findListByParam(query);
		PaginationResultVO<UserAction> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(),list);		
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserAction bean) {
		return this.userActionMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserAction> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.userActionMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserAction> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.userActionMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据ActionId查询
	 */
	public UserAction getUserActionByActionId(Integer actionId) {
		return this.userActionMapper.selectByActionId(actionId);
	}

	/**
	 * 根据ActionId更新
	 */
	public Integer updateUserActionByActionId(UserAction bean, Integer actionId) {
		return this.userActionMapper.updateByActionId(bean,actionId);
	}

	/**
	 * 根据ActionId删除
	 */
	public Integer deleteUserActionByActionId(Integer actionId) {
		return this.userActionMapper.deleteByActionId(actionId);
	}

	/**
	 * 根据VideoIdAnd, CommentIdAnd, ActionTypeAnd, UserId查询
	 */
	public UserAction getUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(String videoId,Integer commentId,Integer actionType,String userId) {
		return this.userActionMapper.selectByVideoIdAndCommentIdAndActionTypeAndUserId(videoId, commentId, actionType, userId);
	}

	/**
	 * 根据VideoIdAnd, CommentIdAnd, ActionTypeAnd, UserId更新
	 */
	public Integer updateUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(UserAction bean, String videoId,Integer commentId,Integer actionType,String userId) {
		return this.userActionMapper.updateByVideoIdAndCommentIdAndActionTypeAndUserId(bean,videoId, commentId, actionType, userId);
	}

	/**
	 * 根据VideoIdAnd, CommentIdAnd, ActionTypeAnd, UserId删除
	 */
	public Integer deleteUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(String videoId,Integer commentId,Integer actionType,String userId) {
		return this.userActionMapper.deleteByVideoIdAndCommentIdAndActionTypeAndUserId(videoId, commentId, actionType, userId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveAction(UserAction bean) {
		VideoInfo videoInfo = videoInfoMapper.selectByVideoId(bean.getVideoId());
		if(videoInfo==null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		bean.setVideoUserId(videoInfo.getUserId());

		UserActionTypeEnum actionTypeEnum = UserActionTypeEnum.getByType(bean.getActionType());
        if(actionTypeEnum==null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserAction dbAction = userActionMapper.selectByVideoIdAndCommentIdAndActionTypeAndUserId(bean.getVideoId(), bean.getCommentId(), bean.getActionType(), bean.getUserId());

		bean.setActionTime(new Date());

		switch(actionTypeEnum) {
			case VIDEO_LIKE:
			case VIDEO_COLLECT:
				if(dbAction!=null) {
					userActionMapper.deleteByActionId(dbAction.getActionId());
				}else{
					userActionMapper.insert(bean);
				}
				Integer changeCount = dbAction == null? Constants.ONE:-Constants.ONE;
				videoInfoMapper.updateCountInfo(bean.getVideoId(),actionTypeEnum.getField(),changeCount);
				if(actionTypeEnum==UserActionTypeEnum.VIDEO_COLLECT){
					esSearchComponent.updateDocCount(videoInfo.getVideoId(), SearchOrderTypeEnum.VIDEO_COLLECT.getField(), changeCount);
				}
				break;
			case VIDEO_COIN:
				if(videoInfo.getUserId().equals(bean.getUserId())){
					throw new BusinessException("UP主不能给自己投币");
				}
				if(dbAction!=null) {
					throw new BusinessException("对本视频投币枚数已用完");
				}
				//减少自己的币
				Integer updateCount = infoMapper.updateCoinCountInfo(bean.getUserId(),-bean.getActionCount());
				if(updateCount==0) {
					throw new BusinessException("币不够");
				}
				//给up加币
				updateCount = infoMapper.updateCoinCountInfo(videoInfo.getUserId(),bean.getActionCount());
				if(updateCount==0){
					throw new BusinessException("投币失败");
				}
				userActionMapper.insert(bean);

				videoInfoMapper.updateCountInfo(bean.getVideoId(),actionTypeEnum.getField(),bean.getActionCount());
				break;
			case COMMENT_LIKE:
			case COMMENT_HATE:
				UserActionTypeEnum opposeTypeEnum = UserActionTypeEnum.COMMENT_LIKE==actionTypeEnum?UserActionTypeEnum.COMMENT_HATE:UserActionTypeEnum.COMMENT_LIKE;

				UserAction opposeAction = userActionMapper.selectByVideoIdAndCommentIdAndActionTypeAndUserId(bean.getVideoId(),bean.getCommentId(),opposeTypeEnum.getType(),bean.getUserId());
				if(opposeAction!=null) {
					userActionMapper.deleteByActionId(opposeAction.getActionId());
				}
				if(dbAction!=null) {
					userActionMapper.deleteByActionId(dbAction.getActionId());
				}else{
					userActionMapper.insert(bean);
				}

				changeCount = dbAction == null? Constants.ONE:-Constants.ONE;
				Integer opposeChangeCount = -changeCount;
				videoCommentMapper.updateCountInfo(bean.getCommentId(),actionTypeEnum.getField(),changeCount,opposeAction==null?null:opposeTypeEnum.getField(),opposeChangeCount);
				break;
		}
	}


}