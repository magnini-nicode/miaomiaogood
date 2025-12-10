package com.easylive.web.task;

import com.easylive.component.EsSearchComponent;
import com.easylive.component.RedisComponent;
import com.easylive.entity.constants.Constants;
import com.easylive.entity.dto.VideoPlayInfoDto;
import com.easylive.entity.po.VideoInfoFilePost;
import com.easylive.enums.SearchOrderTypeEnum;
import com.easylive.redis.RedisUtils;
import com.easylive.service.VideoInfoPostService;
import com.easylive.service.VideoInfoService;
import com.easylive.service.VideoPlayHistoryService;
import com.easylive.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ExecuteQueueTask {
    private ExecutorService executorService = Executors.newFixedThreadPool(Constants.LENGTH_2);

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private VideoInfoPostService videoInfoPostService;

    @Resource
    private VideoInfoService videoInfoService;
    @Resource
    private EsSearchComponent esSearchComponent;

    @Resource
    private VideoPlayHistoryService videoPlayHistoryService;
    @PostConstruct
    public void consumTransferFileQueue(){
        executorService.execute(()->{
            while(true){

                try {
                 VideoInfoFilePost  videoInfoFile=redisComponent.getFileFromTransferQueue();
                    if(videoInfoFile==null){
                        Thread.sleep(1500);
                        continue;
                    }
                    videoInfoPostService.transferVideoFile(videoInfoFile);
                } catch (Exception e) {
                    log.error("获取转码文件队列信息失败",e);

                }
            }
        });
    }

   /* @PostConstruct
    public void consumVideoPlayQueue(){
        executorService.execute(()->{
            while(true){
                try {
                    VideoInfoFilePost videoInfoFile=redisComponent.getFileFromTransferQueue();
                    if(videoInfoFile==null){
                        Thread.sleep(1500);
                        continue;
                    }
                    videoInfoPostService.transferVideoFile(videoInfoFile);
                } catch (Exception e) {
                    log.error("获取转码文件失败",e);
                }
            }
        });
    }*/
   @PostConstruct
   public void consumVideoPlayQueue(){
       executorService.execute(()->{
           while(true){

               try {
                   VideoPlayInfoDto videoPlayInfoDto=redisComponent.getVideoPlayFromVideoPlayQueue();
                   if(videoPlayInfoDto==null){
                       Thread.sleep(1500);
                       continue;
                   }
                   //更新播放数
                   videoInfoService.addReadCount(videoPlayInfoDto.getVideoId());

                   if(!StringTools.isEmpty(videoPlayInfoDto.getUserId())){
                      videoPlayHistoryService.saveHistory(videoPlayInfoDto.getUserId(),videoPlayInfoDto.getVideoId(),videoPlayInfoDto.getFileIndex());
                   }
                   //按天记录视频播放
                   redisComponent.recordVideoPlayCount(videoPlayInfoDto.getVideoId());

                   //更新es播放数量
                   esSearchComponent.updateDocCount(videoPlayInfoDto.getVideoId(), SearchOrderTypeEnum.VIDEO_PLAY.getField(), 1);


               } catch (Exception e) {
                   log.error("获取视频播放文件失败",e);

               }
           }
       });
   }
}
