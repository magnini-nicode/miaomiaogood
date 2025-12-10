package com.easylive.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @Description视频播放历史Mapper
 * @date:2025/10/29
 * @author:magnico
 */
public interface VideoPlayHistoryMapper<T, P> extends BaseMapper {
	/**
	 * 根据UserIdAnd, VideoId查询
	 */
	T selectByUserIdAndVideoId(@Param("userId") String userId,@Param("videoId") String videoId);

	/**
	 * 根据UserIdAnd, VideoId更新
	 */
	Integer updateByUserIdAndVideoId(@Param("bean") T t, @Param("userId") String userId,@Param("videoId") String videoId);

	/**
	 * 根据UserIdAnd, VideoId删除
	 */
	Integer deleteByUserIdAndVideoId(@Param("userId") String userId,@Param("videoId") String videoId);


}