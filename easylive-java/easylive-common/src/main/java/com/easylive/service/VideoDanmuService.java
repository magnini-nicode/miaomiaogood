package com.easylive.service;

import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.po.VideoDanmu;
import com.easylive.entity.query.VideoDanmuQuery;

import java.util.List;
/**
 * @Description视频弹幕Service
 * @date:2025/10/19
 * @author:magnico
 */
public interface VideoDanmuService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoDanmu> findListByParam(VideoDanmuQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoDanmuQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoDanmu> findListByPage(VideoDanmuQuery query);

	/**
	 * 新增
	 */
	Integer add(VideoDanmu bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoDanmu> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoDanmu> listBean);

	/**
	 * 根据DanmuId查询
	 */
	VideoDanmu getVideoDanmuByDanmuId(Integer danmuId);

	/**
	 * 根据DanmuId更新
	 */
	Integer updateVideoDanmuByDanmuId(VideoDanmu bean, Integer danmuId);

	/**
	 * 根据DanmuId删除
	 */
	Integer deleteVideoDanmuByDanmuId(Integer danmuId);

	void saveVideoDanmu(VideoDanmu videoDanmu);

	void deleteDanmu(String userId, Integer danmuId);

}