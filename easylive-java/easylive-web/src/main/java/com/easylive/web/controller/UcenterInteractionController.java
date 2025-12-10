package com.easylive.web.controller;

import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.VideoCommentQuery;
import com.easylive.entity.query.VideoDanmuQuery;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.mappers.VideoInfoMapper;
import com.easylive.service.VideoDanmuService;
import com.easylive.service.VideoInfoService;
import com.easylive.service.impl.VideoCommentServiceImpl;
import com.easylive.web.annotation.GlobalInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/ucenter")
@Validated
@Slf4j
public class UcenterInteractionController extends ABaseController{

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoInfoMapper videoInfoMapper;
    @Resource
    private VideoCommentServiceImpl videoCommentService;
    @Resource
    private VideoDanmuService videoDanmuService;

    @RequestMapping("/loadAllVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadAllVideo(){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();

        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoQuery.setOrderBy("create_time DESC");
        List<VideoInfo> videoInfoList = videoInfoService.findListByParam(videoInfoQuery);
        return getSuccessResponseVO(videoInfoList);

    }

    @RequestMapping("/loadComment")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadComment(Integer pageNo,String videoId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();

        VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
        videoCommentQuery.setVideoId(videoId);
        videoCommentQuery.setUserId(tokenUserInfoDto.getUserId());
        videoCommentQuery.setOrderBy("comment_id DESC");
        videoCommentQuery.setPageNo(pageNo);
        videoCommentQuery.setQueryVideoInfo(true);
        PaginationResultVO resultVO = videoCommentService.findListByPage(videoCommentQuery);

        return getSuccessResponseVO(resultVO);

    }

    @RequestMapping("/delComment")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO delComment(@NotNull Integer commentId){
        videoCommentService.deleteComment(commentId,getTokenUserInfoDto().getUserId());
        return getSuccessResponseVO(null);

    }
    @RequestMapping("/loadDanmu")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadDanmu(Integer pageNo,String videoId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        VideoDanmuQuery danmuQuery = new VideoDanmuQuery();
        danmuQuery.setVideoId(videoId);
        danmuQuery.setVideoUserId(tokenUserInfoDto.getUserId());
        danmuQuery.setOrderBy("danmu_id DESC");
        danmuQuery.setPageNo(pageNo);
        danmuQuery.setQueryVideoInfo(true);
        PaginationResultVO resultVO=videoDanmuService.findListByPage(danmuQuery);
        return getSuccessResponseVO(resultVO);

    }

    @RequestMapping("/delDanmu")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO delDanmu(@NotNull Integer danmuId){
        videoDanmuService.deleteDanmu(getTokenUserInfoDto().getUserId(),danmuId);
        return getSuccessResponseVO(null);

    }
}
