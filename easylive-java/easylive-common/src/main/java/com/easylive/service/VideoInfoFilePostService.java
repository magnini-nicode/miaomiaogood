package com.easylive.service;

import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.po.VideoInfoFilePost;
import com.easylive.entity.query.VideoInfoFilePostQuery;

import java.util.List;



/**
 * @Description视频文件信息Service
 * @date:2025/10/12
 * @author:magnico
 */
public interface VideoInfoFilePostService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoInfoFilePost> findListByParam(VideoInfoFilePostQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoInfoFilePostQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoInfoFilePost> findListByPage(VideoInfoFilePostQuery query);

	/**
	 * 新增
	 */
	Integer add(VideoInfoFilePost bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoInfoFilePost> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoInfoFilePost> listBean);

	/**
	 * 根据FileId查询
	 */
	VideoInfoFilePost getVideoInfoFilePostByFileId(String fileId);

	/**
	 * 根据FileId更新
	 */
	Integer updateVideoInfoFilePostByFileId(VideoInfoFilePost bean, String fileId);

	/**
	 * 根据FileId删除
	 */
	Integer deleteVideoInfoFilePostByFileId(String fileId);

	/**
	 * 根据UploadIdAnd, UserId查询
	 */
	VideoInfoFilePost getVideoInfoFilePostByUploadIdAndUserId(String uploadId,String userId);

	/**
	 * 根据UploadIdAnd, UserId更新
	 */
	Integer updateVideoInfoFilePostByUploadIdAndUserId(VideoInfoFilePost bean, String uploadId,String userId);

	/**
	 * 根据UploadIdAnd, UserId删除
	 */
	Integer deleteVideoInfoFilePostByUploadIdAndUserId(String uploadId,String userId);



}