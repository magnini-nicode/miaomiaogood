package com.easylive.mappers;

import com.easylive.entity.po.UserVideoSeries;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description用户视频序列归档Mapper
 * @date:2025/10/23
 * @author:magnico
 */
public interface UserVideoSeriesMapper<T, P> extends BaseMapper {
	/**
	 * 根据SeriesId查询
	 */
	T selectBySeriesId(@Param("seriesId") Integer seriesId);

	/**
	 * 根据SeriesId更新
	 */
	Integer updateBySeriesId(@Param("bean") T t, @Param("seriesId") Integer seriesId);

	/**
	 * 根据SeriesId删除
	 */
	Integer deleteBySeriesId(@Param("seriesId") Integer seriesId);

	List<T> selectUserAllSeries(@Param("userId") String userId);

	Integer selectMaxSort(@Param("userId") String userId);

	void changeSort(@Param("videoSeriesList") List<UserVideoSeries> videoSeriesList);

	List<T> selectListWithVideo(@Param("query") P p);

}