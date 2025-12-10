package com.easylive.mappers;

import com.easylive.entity.po.CategoryInfo;
import com.easylive.entity.query.CategoryInfoQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description分类信息Mapper
 * @date:2025/10/05
 * @author:magnico
 */
public interface CategoryInfoMapper<T, P> extends BaseMapper<T,P> {
	/**
	 * 根据CategoryId查询
	 */
	T selectByCategoryId(@Param("categoryId") Integer categoryId);

	/**
	 * 根据CategoryId更新
	 */
	Integer updateByCategoryId(@Param("bean") T t, @Param("categoryId") Integer categoryId);

	/**
	 * 根据CategoryId删除
	 */
	Integer deleteByCategoryId(@Param("categoryId") Integer categoryId);

	/**
	 * 根据CategoryCode查询
	 */
	T selectByCategoryCode(@Param("categoryCode") String categoryCode);

	/**
	 * 根据CategoryCode更新
	 */
	Integer updateByCategoryCode(@Param("bean") T t, @Param("categoryCode") String categoryCode);

	/**
	 * 根据CategoryCode删除
	 */
	Integer deleteByCategoryCode(@Param("categoryCode") String categoryCode);

    Integer selectMaxSort(@Param("pCategoryId") Integer pCategoryId);

	void updateSortBatch(@Param("categoryInfoList") List<CategoryInfo> categoryInfoList);
}