package com.easylive.service.impl;

import com.easylive.entity.po.UserVideoSeriesVideo;
import com.easylive.entity.query.UserVideoSeriesVideoQuery;
import com.easylive.entity.query.SimplePage;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.PageSize;
import com.easylive.mappers.UserVideoSeriesVideoMapper;
import com.easylive.service.UserVideoSeriesVideoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @DescriptionService
 * @date:2025/10/23
 * @author:magnico
 */
@Service("userVideoSeriesVideoService")
public class UserVideoSeriesVideoServiceImpl implements UserVideoSeriesVideoService {

	@Resource
	private UserVideoSeriesVideoMapper<UserVideoSeriesVideo, UserVideoSeriesVideoQuery> userVideoSeriesVideoMapper;


	/**
	 * 根据条件查询列表
	 */
	public List<UserVideoSeriesVideo> findListByParam(UserVideoSeriesVideoQuery query) {
		return this.userVideoSeriesVideoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserVideoSeriesVideoQuery query) {
		return this.userVideoSeriesVideoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserVideoSeriesVideo> findListByPage(UserVideoSeriesVideoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserVideoSeriesVideo> list = this.findListByParam(query);
		PaginationResultVO<UserVideoSeriesVideo> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(),list);		
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserVideoSeriesVideo bean) {
		return this.userVideoSeriesVideoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserVideoSeriesVideo> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.userVideoSeriesVideoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserVideoSeriesVideo> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.userVideoSeriesVideoMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据SeriesIdAnd, VideoId查询
	 */
	public UserVideoSeriesVideo getUserVideoSeriesVideoBySeriesIdAndVideoId(Integer seriesId,String videoId) {
		return this.userVideoSeriesVideoMapper.selectBySeriesIdAndVideoId(seriesId, videoId);
	}

	/**
	 * 根据SeriesIdAnd, VideoId更新
	 */
	public Integer updateUserVideoSeriesVideoBySeriesIdAndVideoId(UserVideoSeriesVideo bean, Integer seriesId,String videoId) {
		return this.userVideoSeriesVideoMapper.updateBySeriesIdAndVideoId(bean,seriesId, videoId);
	}

	/**
	 * 根据SeriesIdAnd, VideoId删除
	 */
	public Integer deleteUserVideoSeriesVideoBySeriesIdAndVideoId(Integer seriesId,String videoId) {
		return this.userVideoSeriesVideoMapper.deleteBySeriesIdAndVideoId(seriesId, videoId);
	}


}