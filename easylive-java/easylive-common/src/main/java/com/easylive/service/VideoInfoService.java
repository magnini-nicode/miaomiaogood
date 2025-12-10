package com.easylive.service;

import com.easylive.annotation.RecordUserMessage;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.enums.MessageTypeEnum;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
/**
 * @Description视频信息Service
 * @date:2025/10/12
 * @author:magnico
 */
public interface VideoInfoService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoInfo> findListByParam(VideoInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoInfo> findListByPage(VideoInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(VideoInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoInfo> listBean);

	/**
	 * 根据VideoId查询
	 */
	VideoInfo getVideoInfoByVideoId(String videoId);

	/**
	 * 根据VideoId更新
	 */
	Integer updateVideoInfoByVideoId(VideoInfo bean, String videoId);

	/**
	 * 根据VideoId删除
	 */
	Integer deleteVideoInfoByVideoId(String videoId);

	void changeInteraction (String videoId,String userId,String interaction);

	void deleteVideo(String videoId,String userId);

	void addReadCount(String videoId);

	void recommendVideo(String videoId);
}