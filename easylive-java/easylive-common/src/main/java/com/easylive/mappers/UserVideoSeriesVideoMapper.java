package com.easylive.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @DescriptionMapper
 * @date:2025/10/23
 * @author:magnico
 */
public interface UserVideoSeriesVideoMapper<T, P> extends BaseMapper {
	/**
	 * 根据SeriesIdAnd, VideoId查询
	 */
	T selectBySeriesIdAndVideoId(@Param("seriesId") Integer seriesId,@Param("videoId") String videoId);

	/**
	 * 根据SeriesIdAnd, VideoId更新
	 */
	Integer updateBySeriesIdAndVideoId(@Param("bean") T t, @Param("seriesId") Integer seriesId,@Param("videoId") String videoId);

	/**
	 * 根据SeriesIdAnd, VideoId删除
	 */
	Integer deleteBySeriesIdAndVideoId(@Param("seriesId") Integer seriesId,@Param("videoId") String videoId);

    Integer selectMaxSort(@Param("seriesId")Integer seriesId);

}