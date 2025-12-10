package com.easylive.service;

import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.po.VideoComment;
import com.easylive.entity.query.VideoCommentQuery;

import java.util.List;
/**
 * @Description评论Service
 * @date:2025/10/19
 * @author:magnico
 */
public interface VideoCommentService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoComment> findListByParam(VideoCommentQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoCommentQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoComment> findListByPage(VideoCommentQuery query);

	/**
	 * 新增
	 */
	Integer add(VideoComment bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoComment> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoComment> listBean);

	/**
	 * 根据CommentId查询
	 */
	VideoComment getVideoCommentByCommentId(Integer commentId);

	/**
	 * 根据CommentId更新
	 */
	Integer updateVideoCommentByCommentId(VideoComment bean, Integer commentId);

	/**
	 * 根据CommentId删除
	 */
	Integer deleteVideoCommentByCommentId(Integer commentId);

	void postComment(VideoComment comment,Integer replyCommentId);

	void topComment(Integer commentId,String userId);

	void cancelTopComment(Integer commentId,String userId);

	void deleteComment(Integer commentId,String userId);
}