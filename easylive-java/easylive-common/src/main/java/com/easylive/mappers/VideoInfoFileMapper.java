package com.easylive.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @Description视频文件信息Mapper
 * @date:2025/10/12
 * @author:magnico
 */
public interface VideoInfoFileMapper<T, P> extends BaseMapper<T,P> {
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


}