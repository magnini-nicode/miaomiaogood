package com.easylive.web.controller;

import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.po.VideoInfoFilePost;
import com.easylive.entity.po.VideoInfoPost;
import com.easylive.entity.query.VideoInfoFilePostQuery;
import com.easylive.entity.query.VideoInfoPostQuery;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.entity.vo.VideoPostEditInfoVO;
import com.easylive.entity.vo.VideoStatusCountInfoVO;
import com.easylive.enums.ResponseCodeEnum;
import com.easylive.enums.VideoStatusEnum;
import com.easylive.exception.BusinessException;
import com.easylive.service.VideoInfoFilePostService;
import com.easylive.service.VideoInfoPostService;
import com.easylive.service.VideoInfoService;
import com.easylive.utils.JsonUtils;
import com.easylive.web.annotation.GlobalInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/ucenter")
@Validated
@Slf4j
public class UcenterVideoPostController extends ABaseController{

    @Resource
    private VideoInfoPostService videoInfoPostService;

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;

    @RequestMapping("/postVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO postVideo(String videoId,
                                @NotEmpty String videoCover,
                                @NotEmpty @Size(max=100) String videoName,
                                @NotNull Integer pCategoryId,
                                Integer categoryId,
                                @NotNull Integer postType,
                                @NotEmpty @Size(max=300) String tags,
                                @Size(max=2000) String introduction,
                                @Size(max=3) String interaction,
                                @NotEmpty String uploadFileList) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        List<VideoInfoFilePost> filePostList = JsonUtils.convertJsonArray2List(uploadFileList,VideoInfoFilePost.class);

        VideoInfoPost videoInfo = new VideoInfoPost();
        videoInfo.setVideoId(videoId);
        videoInfo.setVideoCover(videoCover);
        videoInfo.setVideoName(videoName);
        videoInfo.setCategoryId(categoryId);
        videoInfo.setPostType(postType);
        videoInfo.setTags(tags);
        videoInfo.setIntroduction(introduction);
        videoInfo.setInteraction(interaction);
        videoInfo.setUserId(tokenUserInfoDto.getUserId());

        videoInfoPostService.saveVideoInfo(videoInfo,filePostList);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadVideoList")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadVideoPost(Integer status,Integer pageNo,String videoNameFuzzy) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();

        VideoInfoPostQuery videoInfoQuery = new VideoInfoPostQuery();
        videoInfoQuery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoQuery.setOrderBy("v.create_time desc");
        videoInfoQuery.setPageNo(pageNo);
        if(status!=null){
            if(status==-1){
               videoInfoQuery.setExcludeStatusArray(new Integer[]{VideoStatusEnum.STATUS3.getStatus(),VideoStatusEnum.STATUS4.getStatus()});
            }else{
                videoInfoQuery.setStatus(status);
            }
        }
        videoInfoQuery.setVideoNameFuzzy(videoNameFuzzy);
        videoInfoQuery.setQueryCountInfo(true);
        PaginationResultVO resultVO = videoInfoPostService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(resultVO);
    }
    @RequestMapping("/getVideoCountInfo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO getVideoCountInfo() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();

        VideoInfoPostQuery videoInfoPostQuery = new VideoInfoPostQuery();
        videoInfoPostQuery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoPostQuery.setStatus(VideoStatusEnum.STATUS3.getStatus());
        Integer auditPassCount = videoInfoPostService.findCountByParam(videoInfoPostQuery);

        videoInfoPostQuery.setStatus(VideoStatusEnum.STATUS4.getStatus());
        Integer auditFailCount = videoInfoPostService.findCountByParam(videoInfoPostQuery);

        videoInfoPostQuery.setStatus(null);
        videoInfoPostQuery.setExcludeStatusArray(new Integer[]{VideoStatusEnum.STATUS3.getStatus(),VideoStatusEnum.STATUS4.getStatus()});
        Integer inProgress = videoInfoPostService.findCountByParam(videoInfoPostQuery);

        VideoStatusCountInfoVO countInfoVO = new VideoStatusCountInfoVO();
        countInfoVO.setAuditPassCount(auditPassCount);
        countInfoVO.setAuditFailCount(auditFailCount);
        countInfoVO.setInProgress(inProgress);
        return getSuccessResponseVO(countInfoVO);
    }

    @RequestMapping("/getVideoByVideoId")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO getVideoByVideoId(@NotEmpty String videoId) {

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        VideoInfoPost videoInfoPost = this.videoInfoPostService.getVideoInfoPostByVideoId(videoId);
        if(videoInfoPost==null||!videoInfoPost.getUserId().equals(tokenUserInfoDto.getUserId())){
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }

        VideoInfoFilePostQuery videoInfoFilePostQuery = new VideoInfoFilePostQuery();
        videoInfoFilePostQuery.setVideoId(videoId);
        videoInfoFilePostQuery.setOrderBy("file_index asc");
        List<VideoInfoFilePost> videoInfoFilePostList = this.videoInfoFilePostService.findListByParam(videoInfoFilePostQuery);
        VideoPostEditInfoVO vo = new VideoPostEditInfoVO();
        vo.setVideoInfo(videoInfoPost);
        vo.setVideoInfoFileList(videoInfoFilePostList);

        return getSuccessResponseVO(vo);
    }
    @RequestMapping("/saveVideoInteraction")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO saveVideoInteraction(@NotEmpty String videoId,String interaction) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        videoInfoService.changeInteraction(videoId,tokenUserInfoDto.getUserId(),interaction);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/deleteVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO deleteVideo(@NotEmpty String videoId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        videoInfoService.deleteVideo(videoId,tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }
}
