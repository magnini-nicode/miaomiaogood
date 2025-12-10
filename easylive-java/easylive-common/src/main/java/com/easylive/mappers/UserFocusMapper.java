package com.easylive.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @DescriptionMapper
 * @date:2025/10/23
 * @author:magnico
 */
public interface UserFocusMapper<T, P> extends BaseMapper {
	/**
	 * 根据UserIdAnd, FocusUserId查询
	 */
	T selectByUserIdAndFocusUserId(@Param("userId") String userId,@Param("focusUserId") String focusUserId);

	/**
	 * 根据UserIdAnd, FocusUserId更新
	 */
	Integer updateByUserIdAndFocusUserId(@Param("bean") T t, @Param("userId") String userId,@Param("focusUserId") String focusUserId);

	/**
	 * 根据UserIdAnd, FocusUserId删除
	 */
	Integer deleteByUserIdAndFocusUserId(@Param("userId") String userId,@Param("focusUserId") String focusUserId);

    Integer selectFansCount(@Param("userId")String userId);

	Integer selectFocusCount(@Param("userId")String userId);
}