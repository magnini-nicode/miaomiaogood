package com.easylive.service.impl;

import com.easylive.entity.po.VideoPlayHistory;
import com.easylive.entity.query.VideoPlayHistoryQuery;
import com.easylive.entity.query.SimplePage;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.PageSize;
import com.easylive.mappers.VideoPlayHistoryMapper;
import com.easylive.service.VideoPlayHistoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description视频播放历史Service
 * @date:2025/10/29
 * @author:magnico
 */
@Service("videoPlayHistoryService")
public class VideoPlayHistoryServiceImpl implements VideoPlayHistoryService {

	@Resource
	private VideoPlayHistoryMapper<VideoPlayHistory, VideoPlayHistoryQuery> videoPlayHistoryMapper;


	/**
	 * 根据条件查询列表
	 */
	public List<VideoPlayHistory> findListByParam(VideoPlayHistoryQuery query) {
		return this.videoPlayHistoryMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoPlayHistoryQuery query) {
		return this.videoPlayHistoryMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoPlayHistory> findListByPage(VideoPlayHistoryQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<VideoPlayHistory> list = this.findListByParam(query);
		PaginationResultVO<VideoPlayHistory> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(),list);		
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoPlayHistory bean) {
		return this.videoPlayHistoryMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoPlayHistory> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.videoPlayHistoryMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoPlayHistory> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.videoPlayHistoryMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据UserIdAnd, VideoId查询
	 */
	public VideoPlayHistory getVideoPlayHistoryByUserIdAndVideoId(String userId,String videoId) {
		return this.videoPlayHistoryMapper.selectByUserIdAndVideoId(userId, videoId);
	}

	/**
	 * 根据UserIdAnd, VideoId更新
	 */
	public Integer updateVideoPlayHistoryByUserIdAndVideoId(VideoPlayHistory bean, String userId,String videoId) {
		return this.videoPlayHistoryMapper.updateByUserIdAndVideoId(bean,userId, videoId);
	}

	/**
	 * 根据UserIdAnd, VideoId删除
	 */
	public Integer deleteVideoPlayHistoryByUserIdAndVideoId(String userId,String videoId) {
		return this.videoPlayHistoryMapper.deleteByUserIdAndVideoId(userId, videoId);
	}

	@Override
	public Integer deleteByParam(VideoPlayHistoryQuery query) {
		return this.videoPlayHistoryMapper.deleteByParam(query);
	}

	@Override
	public void saveHistory(String userId, String videoId, Integer fileIndex) {
		VideoPlayHistory videoPlayHistory = new VideoPlayHistory();
		videoPlayHistory.setVideoId(videoId);
		videoPlayHistory.setFileIndex(fileIndex);
		videoPlayHistory.setUserId(userId);
		videoPlayHistory.setLastUpdateTime(new Date());
		this.videoPlayHistoryMapper.insertOrUpdate(videoPlayHistory);
	}
}