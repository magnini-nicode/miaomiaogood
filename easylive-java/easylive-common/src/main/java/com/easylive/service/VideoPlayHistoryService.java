package com.easylive.service;

import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.po.VideoPlayHistory;
import com.easylive.entity.query.VideoPlayHistoryQuery;

import java.util.List;
/**
 * @Description视频播放历史Service
 * @date:2025/10/29
 * @author:magnico
 */
public interface VideoPlayHistoryService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoPlayHistory> findListByParam(VideoPlayHistoryQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoPlayHistoryQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoPlayHistory> findListByPage(VideoPlayHistoryQuery query);

	/**
	 * 新增
	 */
	Integer add(VideoPlayHistory bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoPlayHistory> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoPlayHistory> listBean);

	/**
	 * 根据UserIdAnd, VideoId查询
	 */
	VideoPlayHistory getVideoPlayHistoryByUserIdAndVideoId(String userId,String videoId);

	/**
	 * 根据UserIdAnd, VideoId更新
	 */
	Integer updateVideoPlayHistoryByUserIdAndVideoId(VideoPlayHistory bean, String userId,String videoId);

	/**
	 * 根据UserIdAnd, VideoId删除
	 */
	Integer deleteVideoPlayHistoryByUserIdAndVideoId(String userId,String videoId);

	Integer deleteByParam(VideoPlayHistoryQuery query);

	void saveHistory(String userId,String videoId,Integer fileIndex);


}