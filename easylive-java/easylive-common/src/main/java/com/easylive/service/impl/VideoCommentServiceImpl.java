package com.easylive.service.impl;

import com.easylive.entity.constants.Constants;
import com.easylive.entity.po.Info;
import com.easylive.entity.po.VideoComment;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.InfoQuery;
import com.easylive.entity.query.VideoCommentQuery;
import com.easylive.entity.query.SimplePage;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.CommentTopTypeEnum;
import com.easylive.enums.PageSize;
import com.easylive.enums.ResponseCodeEnum;
import com.easylive.enums.UserActionTypeEnum;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.InfoMapper;
import com.easylive.mappers.VideoCommentMapper;
import com.easylive.mappers.VideoInfoMapper;
import com.easylive.service.VideoCommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description评论Service
 * @date:2025/10/19
 * @author:magnico
 */
@Service("videoCommentService")
public class VideoCommentServiceImpl implements VideoCommentService {

	@Resource
	private VideoCommentMapper<VideoComment, VideoCommentQuery> videoCommentMapper;

	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

	@Resource
	private InfoMapper<Info, InfoQuery> infoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<VideoComment> findListByParam(VideoCommentQuery param) {
		if(param.getLoadChildren()!=null&&param.getLoadChildren()){
			return this.videoCommentMapper.selectListWithChildren(param);
		}
		return this.videoCommentMapper.selectList(param);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoCommentQuery query) {
		return this.videoCommentMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoComment> findListByPage(VideoCommentQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<VideoComment> list = this.findListByParam(query);
		PaginationResultVO<VideoComment> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(),list);		
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoComment bean) {
		return this.videoCommentMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoComment> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.videoCommentMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoComment> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.videoCommentMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据CommentId查询
	 */
	public VideoComment getVideoCommentByCommentId(Integer commentId) {
		return this.videoCommentMapper.selectByCommentId(commentId);
	}

	/**
	 * 根据CommentId更新
	 */
	public Integer updateVideoCommentByCommentId(VideoComment bean, Integer commentId) {
		return this.videoCommentMapper.updateByCommentId(bean,commentId);
	}

	/**
	 * 根据CommentId删除
	 */
	public Integer deleteVideoCommentByCommentId(Integer commentId) {
		return this.videoCommentMapper.deleteByCommentId(commentId);
	}

	@Override
	public void postComment(VideoComment comment, Integer replyCommentId) {
		VideoInfo videoInfo = videoInfoMapper.selectByVideoId(comment.getVideoId());
		if(videoInfo==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if(videoInfo.getInteraction()!=null&&videoInfo.getInteraction().contains(Constants.ONE.toString())){
			throw new BusinessException("up主已关闭评论区");
		}
		if(replyCommentId!=null){
			VideoComment replyComment = getVideoCommentByCommentId(replyCommentId);
			if(replyComment==null||!replyComment.getVideoId().equals(comment.getVideoId())){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			if(replyComment.getPCommentId()==0){
                comment.setPCommentId(replyComment.getCommentId());
			}else{
				comment.setPCommentId(replyComment.getPCommentId());
				comment.setReplyUserId(replyComment.getUserId());
			}
			Info userInfo = infoMapper.selectByUserId(replyComment.getUserId());
			comment.setReplyNickName(userInfo.getNickName());
			comment.setReplyAvatar(userInfo.getAvatar());
		}else{
			comment.setPCommentId(0);
		}

		comment.setPostTime(new Date());
		comment.setVideoUserId(videoInfo.getUserId());
		this.videoCommentMapper.insert(comment);
		if(comment.getPCommentId()==0){
			this.videoInfoMapper.updateCountInfo(comment.getVideoId(), UserActionTypeEnum.VIDEO_COMMENT.getField(),1);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void topComment(Integer commentId,String userId) {
         this.cancelTopComment(commentId,userId);
		VideoComment videoComment = new VideoComment();
		videoComment.setTopType(CommentTopTypeEnum.TOP.getType());
		videoCommentMapper.updateByCommentId(videoComment,commentId);
	}
	@Override
	public void cancelTopComment(Integer commentId,String userId) {
        VideoComment dbVideoComment = videoCommentMapper.selectByCommentId(commentId);
		if(dbVideoComment==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		VideoInfo videoInfo = videoInfoMapper.selectByVideoId(dbVideoComment.getVideoId());
		if(videoInfo==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if(!videoInfo.getUserId().equals(userId)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		VideoComment videoComment = new VideoComment();
		videoComment.setTopType(CommentTopTypeEnum.NO_TOP.getType());

		VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
		videoCommentQuery.setVideoId(dbVideoComment.getVideoId());
		videoCommentQuery.setTopType(CommentTopTypeEnum.TOP.getType());
		videoCommentMapper.updateByParam(videoComment,videoCommentQuery);
	}

	@Override
	public void deleteComment(Integer commentId,String userId) {
		VideoComment comment = videoCommentMapper.selectByCommentId(commentId);
		if(comment==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		VideoInfo videoInfo=videoInfoMapper.selectByVideoId(comment.getVideoId());
		if(videoInfo==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if(userId!=null&&!videoInfo.getUserId().equals(userId)&&!comment.getUserId().equals(userId)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		videoCommentMapper.deleteByCommentId(commentId);

		if(comment.getPCommentId()==0){
			videoInfoMapper.updateCountInfo(videoInfo.getVideoId(),UserActionTypeEnum.VIDEO_COMMENT.getField(),-1);
			//删除二级评论
			VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
			videoCommentQuery.setPCommentId(commentId);
			videoCommentMapper.deleteByParam(videoCommentQuery);
		}
	}

}