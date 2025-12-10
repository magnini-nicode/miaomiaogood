package com.easylive.service.impl;

import com.easylive.component.RedisComponent;
import com.easylive.entity.constants.Constants;
import com.easylive.entity.po.StatisticsInfo;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.InfoQuery;
import com.easylive.entity.query.StatisticsInfoQuery;
import com.easylive.entity.query.SimplePage;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.PageSize;
import com.easylive.enums.StatisticsTypeEnum;
import com.easylive.enums.UserActionTypeEnum;
import com.easylive.mappers.InfoMapper;
import com.easylive.mappers.StatisticsInfoMapper;
import com.easylive.mappers.UserFocusMapper;
import com.easylive.mappers.VideoInfoMapper;
import com.easylive.service.StatisticsInfoService;
import com.easylive.utils.DateUtils;
import com.easylive.utils.StringTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description数据统计Service
 * @date:2025/10/29
 * @author:magnico
 */
@Service("statisticsInfoService")
public class StatisticsInfoServiceImpl implements StatisticsInfoService {

	@Resource
	private StatisticsInfoMapper<StatisticsInfo, StatisticsInfoQuery> statisticsInfoMapper;

	@Resource
	private RedisComponent redisComponent;

	@Resource
	private VideoInfoMapper videoInfoMapper;
    @Autowired
    private UserFocusMapper userFocusMapper;
    @Autowired
    private InfoMapper infoMapper;

	/**
	 * 根据条件查询列表
	 */
	public List<StatisticsInfo> findListByParam(StatisticsInfoQuery query) {
		return this.statisticsInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(StatisticsInfoQuery query) {
		return this.statisticsInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<StatisticsInfo> findListByPage(StatisticsInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<StatisticsInfo> list = this.findListByParam(query);
		PaginationResultVO<StatisticsInfo> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(),list);		
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(StatisticsInfo bean) {
		return this.statisticsInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<StatisticsInfo> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.statisticsInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<StatisticsInfo> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.statisticsInfoMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据StatisticsDateAnd, UserIdAnd, DataType查询
	 */
	public StatisticsInfo getStatisticsInfoByStatisticsDateAndUserIdAndDataType(String statisticsDate,String userId,Integer dataType) {
		return this.statisticsInfoMapper.selectByStatisticsDateAndUserIdAndDataType(statisticsDate, userId, dataType);
	}

	/**
	 * 根据StatisticsDateAnd, UserIdAnd, DataType更新
	 */
	public Integer updateStatisticsInfoByStatisticsDateAndUserIdAndDataType(StatisticsInfo bean, String statisticsDate,String userId,Integer dataType) {
		return this.statisticsInfoMapper.updateByStatisticsDateAndUserIdAndDataType(bean,statisticsDate, userId, dataType);
	}

	/**
	 * 根据StatisticsDateAnd, UserIdAnd, DataType删除
	 */
	public Integer deleteStatisticsInfoByStatisticsDateAndUserIdAndDataType(String statisticsDate,String userId,Integer dataType) {
		return this.statisticsInfoMapper.deleteByStatisticsDateAndUserIdAndDataType(statisticsDate, userId, dataType);
	}

	@Override
	public void statisticData() {
		List<StatisticsInfo> statisticsInfoList =new ArrayList<>();
		final String statisticsDate = DateUtils.getBeforeDayDate(1);
		//统计播放数量
		Map<String,Integer> videoPlayCountMap = redisComponent.getVideoPlayCount(statisticsDate);
		List<String> playVideoKeys = new ArrayList<>(videoPlayCountMap.keySet());
		playVideoKeys = playVideoKeys.stream().map(item->item.substring(item.lastIndexOf(":")+1)).collect(Collectors.toList());

		VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
		videoInfoQuery.setVideoIdArray(playVideoKeys.toArray(new String[playVideoKeys.size()]));
		List<VideoInfo> videoInfoList = videoInfoMapper.selectList(videoInfoQuery);

		Map<String,Integer> videoCountMap = videoInfoList.stream().collect(Collectors.groupingBy(VideoInfo::getUserId,
				Collectors.summingInt(item->videoPlayCountMap.get(Constants.REDIS_KEY_VIDEO_PLAY_COUNT+statisticsDate+":"+item.getVideoId()))));

		videoCountMap.forEach((k,v)->{
			StatisticsInfo statisticsInfo = new StatisticsInfo();
			statisticsInfo.setStatisticsDate(statisticsDate);
			statisticsInfo.setUserId(k);
			statisticsInfo.setDataType(StatisticsTypeEnum.PLAY.getType());
			statisticsInfo.setStatisticsCount(v);
			statisticsInfoList.add(statisticsInfo);
		});

		//统计粉丝数
		List<StatisticsInfo> fansDataList = this.statisticsInfoMapper.selectStatisticsFans(statisticsDate);
		for(StatisticsInfo statisticsInfo:fansDataList){
			statisticsInfo.setStatisticsDate(statisticsDate);
			statisticsInfo.setDataType(StatisticsTypeEnum.FANS.getType());
		}
		statisticsInfoList.addAll(fansDataList);

		//统计评论
		List<StatisticsInfo> commentDataList = this.statisticsInfoMapper.selectStatisticsComment(statisticsDate);
		for(StatisticsInfo statisticsInfo:fansDataList){
			statisticsInfo.setStatisticsDate(statisticsDate);
			statisticsInfo.setDataType(StatisticsTypeEnum.COMMENT.getType());
		}
		statisticsInfoList.addAll(commentDataList);

		//弹幕，点赞，收藏，投币
		List<StatisticsInfo> statisticsOthers = this.statisticsInfoMapper.selectStatisticsInfo(statisticsDate,new Integer[]{
				UserActionTypeEnum.VIDEO_LIKE.getType(),UserActionTypeEnum.VIDEO_COIN.getType(),UserActionTypeEnum.VIDEO_COMMENT.getType()
		});

		for(StatisticsInfo statisticsInfo:statisticsOthers){
			statisticsInfo.setStatisticsDate(statisticsDate);
			if(UserActionTypeEnum.VIDEO_LIKE.getType().equals(statisticsInfo.getDataType())){
				statisticsInfo.setDataType(StatisticsTypeEnum.LIKE.getType());
			}else if(UserActionTypeEnum.VIDEO_COLLECT.getType().equals(statisticsInfo.getDataType())){
				statisticsInfo.setDataType(StatisticsTypeEnum.COLLECTION.getType());
			}else if(UserActionTypeEnum.VIDEO_COIN.getType().equals(statisticsInfo.getDataType())){
				statisticsInfo.setDataType(StatisticsTypeEnum.COIN.getType());
			}
		}
		statisticsInfoList.addAll(statisticsOthers);
		this.statisticsInfoMapper.insertOrUpdateBatch(statisticsInfoList);

	}

	@Override
	public Map<String, Integer> getStatisticInfoActualTime(String userId) {
		Map<String, Integer> result = statisticsInfoMapper.selectTotalCountInfo(userId);

		if(!StringTools.isEmpty(userId)){
			result.put("userCount",userFocusMapper.selectFansCount(userId));
		}else{
			result.put("userCount",infoMapper.selectCount(new InfoQuery()));
		}
		return result;
	}

	@Override
	public List<StatisticsInfo> findListTotalInfoByParam(StatisticsInfoQuery query) {
		return this.statisticsInfoMapper.selectListTotalInfoByParam(query);
	}

	@Override
	public List<StatisticsInfo> findUserCountTotalInfoByParam(StatisticsInfoQuery query) {
		return statisticsInfoMapper.selectUserCountTotalInfoByParam(query);
	}
}