package com.easylive.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description视频文件信息Mapper
 * @date:2025/10/12
 * @author:magnico
 */
public interface VideoInfoFilePostMapper<T, P> extends BaseMapper<T,P> {
	/**
	 * 根据FileId查询
	 */
	T selectByFileId(@Param("fileId") String fileId);

	/**
	 * 根据FileId更新
	 */
	Integer updateByFileId(@Param("bean") T t, @Param("fileId") String fileId);

	/**
	 * 根据FileId删除
	 */
	Integer deleteByFileId(@Param("fileId") String fileId);

	/**
	 * 根据UploadIdAnd, UserId查询
	 */
	T selectByUploadIdAndUserId(@Param("uploadId") String uploadId,@Param("userId") String userId);

	/**
	 * 根据UploadIdAnd, UserId更新
	 */
	Integer updateByUploadIdAndUserId(@Param("bean") T t, @Param("uploadId") String uploadId,@Param("userId") String userId);

	/**
	 * 根据UploadIdAnd, UserId删除
	 */
	Integer deleteByUploadIdAndUserId(@Param("uploadId") String uploadId,@Param("userId") String userId);

	void deleteBatchByFileId(@Param("fileList") List<String> fileIdList, @Param("userId") String userId);

	Integer sumDuration(@Param("videoId") String videoId);

}