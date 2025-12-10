package com.easylive.service.impl;

import com.easylive.component.EsSearchComponent;
import com.easylive.component.RedisComponent;
import com.easylive.entity.config.AppConfig;
import com.easylive.entity.dto.SysSettingDto;
import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.po.*;
import com.easylive.entity.query.*;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.PageSize;
import com.easylive.enums.ResponseCodeEnum;
import com.easylive.enums.UserActionTypeEnum;
import com.easylive.enums.VideoRecommendTypeEnum;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.*;
import com.easylive.service.VideoInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description视频信息Service
 * @date:2025/10/12
 * @author:magnico
 */
@Service("videoInfoService")
@Slf4j
public class VideoInfoServiceImpl implements VideoInfoService {

	private static ExecutorService executorService = Executors.newFixedThreadPool(10);

	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

	@Resource
	private VideoInfoPostMapper<VideoInfoPost,VideoInfoPostQuery> videoInfoPostMapper;
    @Resource
    private RedisComponent redisComponent;
	@Resource
	private VideoInfoFileMapper<VideoInfoFile,VideoInfoFileQuery> videoInfoFileMapper;
	@Resource
	private VideoInfoFilePostMapper<VideoInfoFilePost,VideoInfoFilePostQuery> videoInfoFilePostMapper;
	@Resource
	private VideoDanmuMapper<VideoDanmu,VideoDanmuQuery> videoDanmuMapper;
	@Resource
	private VideoCommentMapper<VideoComment,VideoCommentQuery> videoCommentMapper;
	@Resource
	private AppConfig appConfig;
    @Resource
    private EsSearchComponent esSearchComponent;
	@Resource
	private InfoMapper<Info,InfoQuery> infoMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<VideoInfo> findListByParam(VideoInfoQuery query) {
		return this.videoInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoInfoQuery query) {
		return this.videoInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoInfo> findListByPage(VideoInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<VideoInfo> list = this.findListByParam(query);
		PaginationResultVO<VideoInfo> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(),list);		
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoInfo bean) {
		return this.videoInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoInfo> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.videoInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoInfo> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.videoInfoMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据VideoId查询
	 */
	public VideoInfo getVideoInfoByVideoId(String videoId) {
		return this.videoInfoMapper.selectByVideoId(videoId);
	}

	/**
	 * 根据VideoId更新
	 */
	public Integer updateVideoInfoByVideoId(VideoInfo bean, String videoId) {
		return this.videoInfoMapper.updateByVideoId(bean,videoId);
	}

	/**
	 * 根据VideoId删除
	 */
	public Integer deleteVideoInfoByVideoId(String videoId) {
		return this.videoInfoMapper.deleteByVideoId(videoId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changeInteraction(String videoId, String userId, String interaction) {
		VideoInfo videoInfo = new VideoInfo();
		videoInfo.setInteraction(interaction);
		VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
		videoInfoQuery.setUserId(userId);
		videoInfoQuery.setVideoId(videoId);
		videoInfoMapper.updateByParam(videoInfo,videoInfoQuery);

		VideoInfoPost videoInfoPost = new VideoInfoPost();
		videoInfoPost.setInteraction(interaction);
		VideoInfoPostQuery videoInfoPostQuery = new VideoInfoPostQuery();
		videoInfoPostQuery.setUserId(userId);
		videoInfoPostQuery.setVideoId(videoId);
		videoInfoPostMapper.updateByParam(videoInfoPost,videoInfoPostQuery);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteVideo(String videoId, String userId) {
		VideoInfoPost videoInfoPost = this.videoInfoPostMapper.selectByVideoId(videoId);
		if(videoInfoPost==null||userId!=null&&!userId.equals(videoInfoPost.getUserId())){
			throw new BusinessException(ResponseCodeEnum.CODE_404);
		}

		this.videoInfoMapper.deleteByVideoId(videoId);
		this.videoInfoPostMapper.deleteByVideoId(videoId);

		SysSettingDto sysSettingDto = redisComponent.getSysSettingDto();

		infoMapper.updateCoinCountInfo(videoInfoPost.getUserId(),-sysSettingDto.getPostVideoCoinCount());

		esSearchComponent.delDoc(videoId);

		executorService.execute(()->{
			VideoInfoFileQuery videoInfoFileQuery = new VideoInfoFileQuery();
			videoInfoFileQuery.setVideoId(videoId);



			//删除分p
			videoInfoFileMapper.deleteByParam(videoInfoFileQuery);

			VideoInfoFilePostQuery videoInfoFilePost= new  VideoInfoFilePostQuery();
			videoInfoFilePost.setVideoId(videoId);
			videoInfoFilePostMapper.deleteByParam(videoInfoFilePost);

			//删除弹幕
			VideoDanmuQuery videoDanmuQuery = new VideoDanmuQuery();
			videoDanmuQuery.setVideoId(videoId);
			videoDanmuMapper.deleteByParam(videoDanmuQuery);

			//删除评论
			VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
			videoCommentQuery.setVideoId(videoId);
			videoCommentMapper.deleteByParam(videoCommentQuery);

			List<VideoInfoFile> videoInfoFileList = this.videoInfoFileMapper.selectList(videoInfoFileQuery);
			for(VideoInfoFile item:videoInfoFileList){
                try {
                    FileUtils.deleteDirectory(new File(appConfig.getProjectFolder()+item.getFilePath()));
                } catch (IOException e) {
                    log.error("删除文件失败,文件路径:{}",item.getFilePath());
                }
            }

		});
	}

	@Override
	public void addReadCount(String videoId) {
		this.videoInfoMapper.updateCountInfo(videoId, UserActionTypeEnum.VIDEO_PLAY.getField(),1);
	}

	@Override
	public void recommendVideo(String videoId) {
		VideoInfo videoInfo = this.videoInfoMapper.selectByVideoId(videoId);
		if(videoInfo==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		Integer recommendType = null;
		if(VideoRecommendTypeEnum.RECOMMEND.getType().equals(videoInfo.getRecommendType())){
			recommendType = VideoRecommendTypeEnum.NO_RECOMMEND.getType();
		}else{
			recommendType = VideoRecommendTypeEnum.RECOMMEND.getType();
		}
		VideoInfo updateInfo = new VideoInfo();
		updateInfo.setRecommendType(recommendType);
		videoInfoMapper.updateByVideoId(updateInfo,videoId);
	}
}