package com.easylive.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @Description视频弹幕Mapper
 * @date:2025/10/19
 * @author:magnico
 */
public interface VideoDanmuMapper<T, P> extends BaseMapper<T,P> {
	/**
	 * 根据DanmuId查询
	 */
	T selectByDanmuId(@Param("danmuId") Integer danmuId);

	/**
	 * 根据DanmuId更新
	 */
	Integer updateByDanmuId(@Param("bean") T t, @Param("danmuId") Integer danmuId);

	/**
	 * 根据DanmuId删除
	 */
	Integer deleteByDanmuId(@Param("danmuId") Integer danmuId);


}