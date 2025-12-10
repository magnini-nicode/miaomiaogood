package com.easylive.service;

import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.po.StatisticsInfo;
import com.easylive.entity.query.StatisticsInfoQuery;

import java.util.List;
import java.util.Map;

/**
 * @Description数据统计Service
 * @date:2025/10/29
 * @author:magnico
 */
public interface StatisticsInfoService{

	/**
	 * 根据条件查询列表
	 */
	List<StatisticsInfo> findListByParam(StatisticsInfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(StatisticsInfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<StatisticsInfo> findListByPage(StatisticsInfoQuery query);

	/**
	 * 新增
	 */
	Integer add(StatisticsInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<StatisticsInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<StatisticsInfo> listBean);

	/**
	 * 根据StatisticsDateAnd, UserIdAnd, DataType查询
	 */
	StatisticsInfo getStatisticsInfoByStatisticsDateAndUserIdAndDataType(String statisticsDate,String userId,Integer dataType);

	/**
	 * 根据StatisticsDateAnd, UserIdAnd, DataType更新
	 */
	Integer updateStatisticsInfoByStatisticsDateAndUserIdAndDataType(StatisticsInfo bean, String statisticsDate,String userId,Integer dataType);

	/**
	 * 根据StatisticsDateAnd, UserIdAnd, DataType删除
	 */
	Integer deleteStatisticsInfoByStatisticsDateAndUserIdAndDataType(String statisticsDate,String userId,Integer dataType);

	void statisticData();

	Map<String,Integer> getStatisticInfoActualTime(String userId);

	List<StatisticsInfo> findListTotalInfoByParam(StatisticsInfoQuery query);

	List<StatisticsInfo> findUserCountTotalInfoByParam(StatisticsInfoQuery query);
}