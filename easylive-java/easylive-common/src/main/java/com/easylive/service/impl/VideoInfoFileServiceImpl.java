package com.easylive.service.impl;

import com.easylive.entity.po.VideoInfoFile;
import com.easylive.entity.query.VideoInfoFileQuery;
import com.easylive.entity.query.SimplePage;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.PageSize;
import com.easylive.mappers.VideoInfoFileMapper;
import com.easylive.service.VideoInfoFileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description视频文件信息Service
 * @date:2025/10/12
 * @author:magnico
 */
@Service("videoInfoFileService")
public class VideoInfoFileServiceImpl implements VideoInfoFileService {

	@Resource
	private VideoInfoFileMapper<VideoInfoFile, VideoInfoFileQuery> videoInfoFileMapper;


	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<VideoInfoFile> findListByParam(VideoInfoFileQuery query) {
		return this.videoInfoFileMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(VideoInfoFileQuery query) {
		return this.videoInfoFileMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<VideoInfoFile> findListByPage(VideoInfoFileQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<VideoInfoFile> list = this.findListByParam(query);
		PaginationResultVO<VideoInfoFile> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(),list);		
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(VideoInfoFile bean) {
		return this.videoInfoFileMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<VideoInfoFile> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.videoInfoFileMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<VideoInfoFile> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.videoInfoFileMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据FileId查询
	 */
	@Override
	public VideoInfoFile getVideoInfoFileByFileId(String fileId) {
		return this.videoInfoFileMapper.selectByFileId(fileId);
	}

	/**
	 * 根据FileId更新
	 */
	@Override
	public Integer updateVideoInfoFileByFileId(VideoInfoFile bean, String fileId) {
		return this.videoInfoFileMapper.updateByFileId(bean,fileId);
	}

	/**
	 * 根据FileId删除
	 */
	@Override
	public Integer deleteVideoInfoFileByFileId(String fileId) {
		return this.videoInfoFileMapper.deleteByFileId(fileId);
	}


}