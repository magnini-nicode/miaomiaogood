package com.easylive.service.impl;

import com.easylive.entity.po.UserVideoSeries;
import com.easylive.entity.po.UserVideoSeriesVideo;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.UserVideoSeriesQuery;
import com.easylive.entity.query.SimplePage;
import com.easylive.entity.query.UserVideoSeriesVideoQuery;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.PageSize;
import com.easylive.enums.ResponseCodeEnum;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.UserVideoSeriesMapper;
import com.easylive.mappers.UserVideoSeriesVideoMapper;
import com.easylive.mappers.VideoInfoMapper;
import com.easylive.service.UserVideoSeriesService;
import com.easylive.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Description用户视频序列归档Service
 * @date:2025/10/23
 * @author:magnico
 */
@Service("userVideoSeriesService")
public class UserVideoSeriesServiceImpl implements UserVideoSeriesService {

	@Resource
	private UserVideoSeriesMapper<UserVideoSeries, UserVideoSeriesQuery> userVideoSeriesMapper;

	@Resource
	private VideoInfoMapper<VideoInfo,VideoInfoQuery> videoInfoMapper;

	@Resource
	private UserVideoSeriesVideoMapper<UserVideoSeriesVideo, UserVideoSeriesVideoQuery> userVideoSeriesVideoMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<UserVideoSeries> findListByParam(UserVideoSeriesQuery query) {
		return this.userVideoSeriesMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserVideoSeriesQuery query) {
		return this.userVideoSeriesMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserVideoSeries> findListByPage(UserVideoSeriesQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserVideoSeries> list = this.findListByParam(query);
		PaginationResultVO<UserVideoSeries> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(),list);		
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserVideoSeries bean) {
		return this.userVideoSeriesMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserVideoSeries> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.userVideoSeriesMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserVideoSeries> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.userVideoSeriesMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据SeriesId查询
	 */
	public UserVideoSeries getUserVideoSeriesBySeriesId(Integer seriesId) {
		return this.userVideoSeriesMapper.selectBySeriesId(seriesId);
	}

	/**
	 * 根据SeriesId更新
	 */
	public Integer updateUserVideoSeriesBySeriesId(UserVideoSeries bean, Integer seriesId) {
		return this.userVideoSeriesMapper.updateBySeriesId(bean,seriesId);
	}

	/**
	 * 根据SeriesId删除
	 */
	public Integer deleteUserVideoSeriesBySeriesId(Integer seriesId) {
		return this.userVideoSeriesMapper.deleteBySeriesId(seriesId);
	}

	@Override
	public List<UserVideoSeries> getUserAllSeries(String userId) {
		return userVideoSeriesMapper.selectUserAllSeries(userId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveUserVideoSeries(UserVideoSeries bean,String videoIds){
		if(bean.getSeriesId()==null&& StringTools.isEmpty(videoIds)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if(bean.getSeriesId()==null){
			checkVideoIds(bean.getUserId(),videoIds);

			bean.setUpdateTime(new Date());
			bean.setSort(this.userVideoSeriesMapper.selectMaxSort(bean.getUserId())+1);
			this.userVideoSeriesMapper.insert(bean);

			this.saveSeriesVideo(bean.getUserId(),bean.getSeriesId(),videoIds);
		}else{
			UserVideoSeriesQuery seriesVideoQuery = new UserVideoSeriesQuery();
			seriesVideoQuery.setUserId(bean.getUserId());
			seriesVideoQuery.setSeriesId(bean.getSeriesId());

			this.userVideoSeriesMapper.updateByParam(bean,seriesVideoQuery);
		}
	}



	private void checkVideoIds(String userId,String videoIds){
		String[] videoIdArray = videoIds.split(",");
		VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
		videoInfoQuery.setVideoIdArray(videoIdArray);
		videoInfoQuery.setUserId(userId);
		Integer count = videoInfoMapper.selectCount(videoInfoQuery);
		if(videoIdArray.length!=count){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
	}

	@Override
	public void saveSeriesVideo(String userId,Integer seriesId,String videoIds){
		UserVideoSeries userVideoSeries = getUserVideoSeriesBySeriesId(seriesId);
		if(userVideoSeries==null||!userVideoSeries.getUserId().equals(userId)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		this.checkVideoIds(userId,videoIds);

		String[] videoIdArray = videoIds.split(",");
		Integer sort = this.userVideoSeriesVideoMapper.selectMaxSort(seriesId);

		List<UserVideoSeriesVideo> seriesVideoList = new ArrayList<>();
		for(String videoId:videoIdArray){
			UserVideoSeriesVideo videoSeriesVideo = new UserVideoSeriesVideo();
			videoSeriesVideo.setVideoId(videoId);
			videoSeriesVideo.setSort(++sort);
			videoSeriesVideo.setSeriesId(seriesId);
			videoSeriesVideo.setUserId(userId);
			seriesVideoList.add(videoSeriesVideo);
		}
		this.userVideoSeriesVideoMapper.insertOrUpdateBatch(seriesVideoList);
	}

	@Override
	public void delSeriesVideo(String userId, Integer seriesId, String videoId) {
		UserVideoSeriesVideoQuery videoSeriesVideoQuery = new UserVideoSeriesVideoQuery();
		videoSeriesVideoQuery.setUserId(userId);
		videoSeriesVideoQuery.setSeriesId(seriesId);
		videoSeriesVideoQuery.setVideoId(videoId);
		this.userVideoSeriesVideoMapper.deleteByParam(videoSeriesVideoQuery);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delVideoSeries(String userId,Integer seriesId) {
		UserVideoSeriesQuery seriesQuery = new UserVideoSeriesQuery();
		seriesQuery.setUserId(userId);
		seriesQuery.setSeriesId(seriesId);
		Integer count = userVideoSeriesMapper.deleteByParam(seriesQuery);
		if(count==0){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserVideoSeriesVideoQuery videoSeriesVideoQuery = new UserVideoSeriesVideoQuery();
		videoSeriesVideoQuery.setUserId(userId);
		videoSeriesVideoQuery.setSeriesId(seriesId);
		userVideoSeriesVideoMapper.deleteByParam(videoSeriesVideoQuery);
	}

	@Override
	public void changeVideoSeriesSort(String userId, String seriesIds) {
		String[] seriesIdArray = seriesIds.split(",");
		List<UserVideoSeries> videoSeriesList = new ArrayList<>();
		Integer sort = 0;
		for(String seriesId:seriesIdArray){
			UserVideoSeries videoSeries = new UserVideoSeries();
			videoSeries.setUserId(userId);
			videoSeries.setSeriesId(Integer.parseInt(seriesId));
			videoSeries.setSort(sort++);
			videoSeriesList.add(videoSeries);
		}
		userVideoSeriesMapper.changeSort(videoSeriesList);
	}

	@Override
	public List<UserVideoSeries> findListWithVideoList(UserVideoSeriesQuery seriesQuery) {
		return userVideoSeriesMapper.selectListWithVideo(seriesQuery);
	}
}