package com.easylive.service;

import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.po.UserFocus;
import com.easylive.entity.query.UserFocusQuery;

import java.util.List;
/**
 * @DescriptionService
 * @date:2025/10/23
 * @author:magnico
 */
public interface UserFocusService{

	/**
	 * 根据条件查询列表
	 */
	List<UserFocus> findListByParam(UserFocusQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserFocusQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserFocus> findListByPage(UserFocusQuery query);

	/**
	 * 新增
	 */
	Integer add(UserFocus bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserFocus> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserFocus> listBean);

	/**
	 * 根据UserIdAnd, FocusUserId查询
	 */
	UserFocus getUserFocusByUserIdAndFocusUserId(String userId,String focusUserId);

	/**
	 * 根据UserIdAnd, FocusUserId更新
	 */
	Integer updateUserFocusByUserIdAndFocusUserId(UserFocus bean, String userId,String focusUserId);

	/**
	 * 根据UserIdAnd, FocusUserId删除
	 */
	Integer deleteUserFocusByUserIdAndFocusUserId(String userId,String focusUserId);

	void focusUser(String userId,String focusUserId);

	void cancelFocus(String userId,String focusUserId);

}