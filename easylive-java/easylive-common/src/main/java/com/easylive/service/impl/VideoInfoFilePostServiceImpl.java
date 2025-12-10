package com.easylive.service.impl;

import com.easylive.entity.po.VideoInfoFilePost;
import com.easylive.entity.query.VideoInfoFilePostQuery;
import com.easylive.entity.query.SimplePage;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.PageSize;
import com.easylive.mappers.VideoInfoFilePostMapper;
import com.easylive.service.VideoInfoFilePostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**

 *
 * @Description视频文件信息Service
 * @date:2025/10/12
 * @author:magnico
 */
@Service("videoInfoFilePostService")
public class VideoInfoFilePostServiceImpl implements VideoInfoFilePostService{

	@Resource
	private VideoInfoFilePostMapper<VideoInfoFilePost, VideoInfoFilePostQuery> videoInfoFilePostMapper;


	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<VideoInfoFilePost> findListByParam(VideoInfoFilePostQuery query) {
		return this.videoInfoFilePostMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(VideoInfoFilePostQuery query) {
		return this.videoInfoFilePostMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<VideoInfoFilePost> findListByPage(VideoInfoFilePostQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<VideoInfoFilePost> list = this.findListByParam(query);
		PaginationResultVO<VideoInfoFilePost> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(),list);		
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(VideoInfoFilePost bean) {
		return this.videoInfoFilePostMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<VideoInfoFilePost> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.videoInfoFilePostMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<VideoInfoFilePost> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.videoInfoFilePostMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据FileId查询
	 */
	@Override
	public VideoInfoFilePost getVideoInfoFilePostByFileId(String fileId) {
		return this.videoInfoFilePostMapper.selectByFileId(fileId);
	}

	/**
	 * 根据FileId更新
	 */
	@Override
	public Integer updateVideoInfoFilePostByFileId(VideoInfoFilePost bean, String fileId) {
		return this.videoInfoFilePostMapper.updateByFileId(bean,fileId);
	}

	/**
	 * 根据FileId删除
	 */
	@Override
	public Integer deleteVideoInfoFilePostByFileId(String fileId) {
		return this.videoInfoFilePostMapper.deleteByFileId(fileId);
	}

	/**
	 * 根据UploadIdAnd, UserId查询
	 */
	@Override
	public VideoInfoFilePost getVideoInfoFilePostByUploadIdAndUserId(String uploadId,String userId) {
		return this.videoInfoFilePostMapper.selectByUploadIdAndUserId(uploadId, userId);
	}

	/**
	 * 根据UploadIdAnd, UserId更新
	 */
	@Override
	public Integer updateVideoInfoFilePostByUploadIdAndUserId(VideoInfoFilePost bean, String uploadId,String userId) {
		return this.videoInfoFilePostMapper.updateByUploadIdAndUserId(bean,uploadId, userId);
	}

	/**
	 * 根据UploadIdAnd, UserId删除
	 */
	@Override
	public Integer deleteVideoInfoFilePostByUploadIdAndUserId(String uploadId,String userId) {
		return this.videoInfoFilePostMapper.deleteByUploadIdAndUserId(uploadId, userId);
	}


}