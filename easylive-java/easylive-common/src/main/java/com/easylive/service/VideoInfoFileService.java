package com.easylive.service;

import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.po.VideoInfoFile;
import com.easylive.entity.query.VideoInfoFileQuery;

import java.util.List;
/**
 * @Description视频文件信息Service
 * @date:2025/10/12
 * @author:magnico
 */
public interface VideoInfoFileService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoInfoFile> findListByParam(VideoInfoFileQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoInfoFileQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoInfoFile> findListByPage(VideoInfoFileQuery query);

	/**
	 * 新增
	 */
	Integer add(VideoInfoFile bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoInfoFile> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoInfoFile> listBean);

	/**
	 * 根据FileId查询
	 */
	VideoInfoFile getVideoInfoFileByFileId(String fileId);

	/**
	 * 根据FileId更新
	 */
	Integer updateVideoInfoFileByFileId(VideoInfoFile bean, String fileId);

	/**
	 * 根据FileId删除
	 */
	Integer deleteVideoInfoFileByFileId(String fileId);

}