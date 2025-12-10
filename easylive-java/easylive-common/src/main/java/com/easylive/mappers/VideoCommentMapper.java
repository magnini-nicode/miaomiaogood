package com.easylive.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description评论Mapper
 * @date:2025/10/19
 * @author:magnico
 */
public interface VideoCommentMapper<T, P> extends BaseMapper<T,P> {
	/**
	 * 根据CommentId查询
	 */
	T selectByCommentId(@Param("commentId") Integer commentId);

	/**
	 * 根据CommentId更新
	 */
	Integer updateByCommentId(@Param("bean") T t, @Param("commentId") Integer commentId);

	/**
	 * 根据CommentId删除
	 */
	Integer deleteByCommentId(@Param("commentId") Integer commentId);

    List<T> selectListWithChildren(@Param("query") P p);

	void updateCountInfo(@Param("commentId")Integer commentId,@Param("field")String field,@Param("changeCount")Integer changeCount,
						 @Param("opposeField")String opposeField,@Param("opposeChangeCount")Integer opposeChangeCount);
}