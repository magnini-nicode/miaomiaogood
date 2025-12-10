package com.easylive.web.controller;

import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.po.UserVideoSeries;
import com.easylive.entity.po.UserVideoSeriesVideo;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.UserVideoSeriesQuery;
import com.easylive.entity.query.UserVideoSeriesVideoQuery;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.entity.vo.UserVideoSeriesDetailVO;
import com.easylive.enums.ResponseCodeEnum;
import com.easylive.exception.BusinessException;
import com.easylive.service.*;
import com.easylive.web.annotation.GlobalInterceptor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/uhome/series")
@Validated
public class UHomeVideoSeriesController extends  ABaseController{
   @Resource
    private VideoInfoService videoInfoService;

   @Resource
    private UserVideoSeriesService userVideoSeriesService;

   @Resource
    private UserVideoSeriesVideoService userVideoSeriesVideoService;

   @RequestMapping("/loadVideoSeries")
    public ResponseVO loadVideoSeries(@NotEmpty String userId){
       List<UserVideoSeries> videoSeries = userVideoSeriesService.getUserAllSeries(userId);
       return getSuccessResponseVO(videoSeries);
   }

    @RequestMapping("/saveVideoSeries")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO saveVideoSeries(Integer seriesId,
                                      @NotEmpty @Size(max=100) String seriesName,
                                      @Size(max=200) String seriesDescription,
                                      String videoIds){

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();

        UserVideoSeries videoSeries = new UserVideoSeries();
        videoSeries.setUserId(tokenUserInfoDto.getUserId());
        videoSeries.setSeriesId(seriesId);
        videoSeries.setSeriesName(seriesName);
        videoSeries.setSeriesDescription(seriesDescription);
        this.userVideoSeriesService.saveUserVideoSeries(videoSeries,videoIds);
        return getSuccessResponseVO(videoSeries);
    }

    @RequestMapping("/loadAllVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadAllVideo(Integer seriesId){

       TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
       VideoInfoQuery infoQuery = new VideoInfoQuery();
       if(seriesId!=null){
           UserVideoSeriesVideoQuery videoSeriesVideoQuery = new UserVideoSeriesVideoQuery();
           videoSeriesVideoQuery.setSeriesId(seriesId);
           videoSeriesVideoQuery.setUserId(tokenUserInfoDto.getUserId());
           List<UserVideoSeriesVideo> seriesVideoList = userVideoSeriesVideoService.findListByParam(videoSeriesVideoQuery);

           List<String> videoIdList = seriesVideoList.stream().map(item->item.getVideoId()).collect(Collectors.toList());
           infoQuery.setExcludeVideoIdArray(videoIdList.toArray(new String[videoIdList.size()]));
       }

       infoQuery.setUserId(tokenUserInfoDto.getUserId());
       List<VideoInfo> videoInfoList = videoInfoService.findListByParam(infoQuery);
        return getSuccessResponseVO(videoInfoList);
    }

    @RequestMapping("/getVideoSeriesDetail")
    public ResponseVO getVideoSeriesDetail(@NotNull Integer seriesId){

       UserVideoSeries videoSeries = userVideoSeriesService.getUserVideoSeriesBySeriesId(seriesId);
       if(videoSeries==null){
           throw new BusinessException(ResponseCodeEnum.CODE_404);
       }

       UserVideoSeriesVideoQuery videoSeriesVideoQuery = new UserVideoSeriesVideoQuery();
       videoSeriesVideoQuery.setOrderBy("sort asc");
       videoSeriesVideoQuery.setQueryVideoInfo(true);
       videoSeriesVideoQuery.setSeriesId(seriesId);
       List<UserVideoSeriesVideo> seriesVideoList = userVideoSeriesVideoService.findListByParam(videoSeriesVideoQuery);

        return getSuccessResponseVO(new UserVideoSeriesDetailVO(videoSeries,seriesVideoList));
    }
    @RequestMapping("/saveSeriesVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO saveSeriesVideo(@NotNull Integer seriesId,@NotEmpty String videoIds){

       TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
       this.userVideoSeriesService.saveSeriesVideo(tokenUserInfoDto.getUserId(),seriesId,videoIds);
       return getSuccessResponseVO(null);
    }
    @RequestMapping("/delSeriesVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO delSeriesVideo(@NotNull Integer seriesId,@NotEmpty String videoId){

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        this.userVideoSeriesService.delSeriesVideo(tokenUserInfoDto.getUserId(),seriesId,videoId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delVideoSeries")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO delVideoSeries(@NotNull Integer seriesId){

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        this.userVideoSeriesService.delVideoSeries(tokenUserInfoDto.getUserId(),seriesId);
        return getSuccessResponseVO(null);
    }
    @RequestMapping("/changeVideoSeriesSort")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO changeVideoSeriesSort(@NotNull String seriesIds){

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        this.userVideoSeriesService.changeVideoSeriesSort(tokenUserInfoDto.getUserId(),seriesIds);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadVideoSeriesWithVideo")
    public ResponseVO loadVideoSeriesWithVideo(@NotNull String userId){
        UserVideoSeriesQuery seriesQuery = new UserVideoSeriesQuery();
        seriesQuery.setUserId(userId);
        seriesQuery.setOrderBy("sort asc");
        List<UserVideoSeries> videoSeries = userVideoSeriesService.findListWithVideoList(seriesQuery);
        return getSuccessResponseVO(videoSeries);
    }
}
