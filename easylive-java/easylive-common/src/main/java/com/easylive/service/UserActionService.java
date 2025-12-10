package com.easylive.service;

import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.po.UserAction;
import com.easylive.entity.query.UserActionQuery;

import java.util.List;
/**
 * @Description用户行为 点赞 评论Service
 * @date:2025/10/19
 * @author:magnico
 */
public interface UserActionService{

	/**
	 * 根据条件查询列表
	 */
	List<UserAction> findListByParam(UserActionQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserActionQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserAction> findListByPage(UserActionQuery query);

	/**
	 * 新增
	 */
	Integer add(UserAction bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserAction> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserAction> listBean);

	/**
	 * 根据ActionId查询
	 */
	UserAction getUserActionByActionId(Integer actionId);

	/**
	 * 根据ActionId更新
	 */
	Integer updateUserActionByActionId(UserAction bean, Integer actionId);

	/**
	 * 根据ActionId删除
	 */
	Integer deleteUserActionByActionId(Integer actionId);

	/**
	 * 根据VideoIdAnd, CommentIdAnd, ActionTypeAnd, UserId查询
	 */
	UserAction getUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(String videoId,Integer commentId,Integer actionType,String userId);

	/**
	 * 根据VideoIdAnd, CommentIdAnd, ActionTypeAnd, UserId更新
	 */
	Integer updateUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(UserAction bean, String videoId,Integer commentId,Integer actionType,String userId);

	/**
	 * 根据VideoIdAnd, CommentIdAnd, ActionTypeAnd, UserId删除
	 */
	Integer deleteUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(String videoId,Integer commentId,Integer actionType,String userId);

	void saveAction(UserAction userAction);
}