package com.easylive.mappers;

import com.easylive.entity.po.StatisticsInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description数据统计Mapper
 * @date:2025/10/29
 * @author:magnico
 */
public interface StatisticsInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据StatisticsDateAnd, UserIdAnd, DataType查询
	 */
	T selectByStatisticsDateAndUserIdAndDataType(@Param("statisticsDate") String statisticsDate,@Param("userId") String userId,@Param("dataType") Integer dataType);

	/**
	 * 根据StatisticsDateAnd, UserIdAnd, DataType更新
	 */
	Integer updateByStatisticsDateAndUserIdAndDataType(@Param("bean") T t, @Param("statisticsDate") String statisticsDate,@Param("userId") String userId,@Param("dataType") Integer dataType);

	/**
	 * 根据StatisticsDateAnd, UserIdAnd, DataType删除
	 */
	Integer deleteByStatisticsDateAndUserIdAndDataType(@Param("statisticsDate") String statisticsDate,@Param("userId") String userId,@Param("dataType") Integer dataType);

	List<T> selectStatisticsFans(@Param("statisticsData") String statisticsData);

	List<T> selectStatisticsComment(@Param("statisticsData") String statisticsData);

	List<T> selectStatisticsInfo(@Param("statisticsData") String statisticsData,@Param("actionTypeArray") Integer[]  actionType);

	Map<String,Integer> selectTotalCountInfo(@Param("userId")String userId);

	List<T> selectListTotalInfoByParam(@Param("query") P p);

	List<T> selectUserCountTotalInfoByParam(@Param("query") P p);
}