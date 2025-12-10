package com.easylive.admin.controller;

import com.easylive.entity.query.VideoCommentQuery;
import com.easylive.entity.query.VideoDanmuQuery;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.VideoCommentService;
import com.easylive.service.VideoDanmuService;
import com.easylive.service.impl.VideoCommentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/interact")
@Validated
@Slf4j
public class InteractController extends ABaseController {

    @Resource
    private VideoCommentService videoCommentService;

    @Resource
    private VideoDanmuService videoDanmuService;

    @RequestMapping("/loadComment")
    public ResponseVO loadComment(Integer pageNo,String videoNameFuzzy){
        VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
        videoCommentQuery.setPageNo(pageNo);
        videoCommentQuery.setOrderBy("comment_id desc");
        videoCommentQuery.setQueryVideoInfo(true);
        videoCommentQuery.setVideoNameFuzzy(videoNameFuzzy);
        PaginationResultVO resultVO = videoCommentService.findListByPage(videoCommentQuery);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/delComment")
    public ResponseVO delComment(@NotNull Integer commentId){
       videoCommentService.deleteComment(commentId,null);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadDanmu")
    public ResponseVO loadDanmu(Integer pageNo,String videoNameFuzzy){
        VideoDanmuQuery danmuQuery = new VideoDanmuQuery();
        danmuQuery.setOrderBy("danmu_id desc");
        danmuQuery.setPageNo(pageNo);
        danmuQuery.setQueryVideoInfo(true);
        danmuQuery.setVideoNameFuzzy(videoNameFuzzy);
        PaginationResultVO resultVO = videoDanmuService.findListByPage(danmuQuery);
        return getSuccessResponseVO(resultVO);
    }
    @RequestMapping("/delDanmu")
    public ResponseVO delDanmu(@NotNull Integer danmuId){
        videoDanmuService.deleteDanmu(null,danmuId);
        return getSuccessResponseVO(null);
    }
}
