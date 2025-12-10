package com.easylive.service;

import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.po.UserVideoSeries;
import com.easylive.entity.query.UserVideoSeriesQuery;

import java.util.List;
/**
 * @Description用户视频序列归档Service
 * @date:2025/10/23
 * @author:magnico
 */
public interface UserVideoSeriesService{

	/**
	 * 根据条件查询列表
	 */
	List<UserVideoSeries> findListByParam(UserVideoSeriesQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserVideoSeriesQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserVideoSeries> findListByPage(UserVideoSeriesQuery query);

	/**
	 * 新增
	 */
	Integer add(UserVideoSeries bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserVideoSeries> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserVideoSeries> listBean);

	/**
	 * 根据SeriesId查询
	 */
	UserVideoSeries getUserVideoSeriesBySeriesId(Integer seriesId);

	/**
	 * 根据SeriesId更新
	 */
	Integer updateUserVideoSeriesBySeriesId(UserVideoSeries bean, Integer seriesId);

	/**
	 * 根据SeriesId删除
	 */
	Integer deleteUserVideoSeriesBySeriesId(Integer seriesId);

	List<UserVideoSeries> getUserAllSeries(String userId);

	void saveUserVideoSeries(UserVideoSeries videoSeries,String videoIds);

	void saveSeriesVideo(String userId,Integer seriesId,String videoIds);

	void delSeriesVideo(String userId,Integer seriesId,String videoId);

	void delVideoSeries(String userId,Integer seriesId);

	void changeVideoSeriesSort(String userId,String seriesIds);

	List<UserVideoSeries> findListWithVideoList(UserVideoSeriesQuery seriesQuery);

}