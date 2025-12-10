package com.easylive.service.impl;

import com.easylive.component.RedisComponent;
import com.easylive.entity.constants.Constants;
import com.easylive.entity.dto.CountInfoDto;
import com.easylive.entity.dto.SysSettingDto;
import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.dto.UserCountInfoDto;
import com.easylive.entity.po.Info;
import com.easylive.entity.po.UserFocus;
import com.easylive.entity.query.InfoQuery;
import com.easylive.entity.query.SimplePage;
import com.easylive.entity.query.UserFocusQuery;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.PageSize;
import com.easylive.enums.ResponseCodeEnum;
import com.easylive.enums.UserSexEnum;
import com.easylive.enums.UserStatusEnum;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.InfoMapper;
import com.easylive.mappers.UserFocusMapper;
import com.easylive.mappers.VideoInfoMapper;
import com.easylive.service.InfoService;
import com.easylive.service.UserFocusService;
import com.easylive.utils.CopyTools;
import com.easylive.utils.StringTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @Description用户信息Service
 * @date:2025/09/28
 * @author:magnico
 */
@Service("infoService")
public class InfoServiceImpl implements InfoService {

	@Resource
	private InfoMapper<Info, InfoQuery> infoMapper;

    @Resource
	private RedisComponent redisComponent;

	@Resource
	private UserFocusMapper<UserFocus, UserFocusQuery> userFocusMapper;
    @Autowired
    private VideoInfoMapper videoInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Info> findListByParam(InfoQuery query) {
		return this.infoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(InfoQuery query) {
		return this.infoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<Info> findListByPage(InfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<Info> list = this.findListByParam(query);
		PaginationResultVO<Info> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(),list);		
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(Info bean) {
		return this.infoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<Info> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.infoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<Info> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.infoMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据UserId查询
	 */
	@Override
	public Info getInfoByUserId(String userId) {
		return this.infoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId更新
	 */
	@Override
	public Integer updateInfoByUserId(Info bean, String userId) {
		return this.infoMapper.updateByUserId(bean,userId);
	}

	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteInfoByUserId(String userId) {
		return this.infoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email查询
	 */
	@Override
	public Info getInfoByEmail(String email) {
		return this.infoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email更新
	 */
	@Override
	public Integer updateInfoByEmail(Info bean, String email) {
		return this.infoMapper.updateByEmail(bean,email);
	}

	/**
	 * 根据Email删除
	 */
	@Override
	public Integer deleteInfoByEmail(String email) {
		return this.infoMapper.deleteByEmail(email);
	}

	/**
	 * 根据NickName查询
	 */
	@Override
	public Info getInfoByNickName(String nickName) {
		return this.infoMapper.selectByNickName(nickName);
	}

	/**
	 * 根据NickName更新
	 */
	@Override
	public Integer updateInfoByNickName(Info bean, String nickName) {
		return this.infoMapper.updateByNickName(bean,nickName);
	}

	/**
	 * 根据NickName删除
	 */
	@Override
	public Integer deleteInfoByNickName(String nickName) {
		return this.infoMapper.deleteByNickName(nickName);
	}
    @Override
    public void register(String email,String nickName,String registerPassword) {

		Info info = this.infoMapper.selectByEmail(email);
		if (null!=info) {
			throw new BusinessException("邮箱账号已经存在");

		}
		Info nickNameUser = this.infoMapper.selectByNickName(nickName);
		if(null!=nickNameUser){
			throw new BusinessException("昵称已经存在");
		}
		info = new Info();
		String userId = StringTools.getRandomNumber(Constants.LENGTH_10);
		info.setUserId(userId);
		info.setNickName(nickName);
		info.setEmail(email);
		info.setPassword(StringTools.encodeByMd5(registerPassword));
		info.setJoinTime(new Date());
		info.setStatus(UserStatusEnum.ENABLE.getStatus());
		info.setSex(UserSexEnum.SECRECY.getType());
		info.setTheme(Constants.ONE);

		SysSettingDto sysSettingDto = redisComponent.getSysSettingDto();


		info.setCurrentCoinCount(sysSettingDto.getRegisterCoinCount());
		info.setTotalCoinCount(sysSettingDto.getRegisterCoinCount());
		this.infoMapper.insert(info);
	}
	@Override
	public TokenUserInfoDto login(String email, String password, String ip) {
		Info info = this.infoMapper.selectByEmail(email);
		if(null==info){
			throw new BusinessException("账号不存在");
		}
		if(!info.getPassword().equalsIgnoreCase(password)){
			throw new BusinessException("密码错误");
		}
		if(UserStatusEnum.DISABLE.getStatus().equals(info.getStatus())){
			throw new BusinessException("账号已经禁用");
		}
		Info updateInfo = new Info();
		updateInfo.setLastLoginTime(new Date());
		updateInfo.setLastLoginIp(ip);
		this.infoMapper.updateByUserId(updateInfo,info.getUserId());

		TokenUserInfoDto tokenUserInfoDto = CopyTools.copy(info,TokenUserInfoDto.class);
		redisComponent.saveTokenInfo(tokenUserInfoDto);
		return tokenUserInfoDto;

	}
	@Override
	public Info getUserDetailInfo(String currentUserId,String userId) {
		Info userInfo = getInfoByUserId(userId);
		if(null==userInfo){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		CountInfoDto countInfoDto = videoInfoMapper.selectSumCountInfo(userId);

		CopyTools.copyProperties(countInfoDto,userInfo);

		Integer fansCount = userFocusMapper.selectFansCount(userId);
		Integer focusCount = userFocusMapper.selectFocusCount(userId);
		userInfo.setFansCount(fansCount);
		userInfo.setFocusCount(focusCount);

		if(currentUserId==null){
			userInfo.setHaveFocus(false);
		}else{
			UserFocus userFocus = userFocusMapper.selectByUserIdAndFocusUserId(currentUserId,userId);
			userInfo.setHaveFocus(userFocus==null?false:true);
		}

		return userInfo;
	}

	@Override
	@Transactional
	public void updateUserInfo(Info userInfo,TokenUserInfoDto tokenUserInfoDto) {
		Info dbInfo = this.infoMapper.selectByUserId(userInfo.getUserId());
		if(!dbInfo.getNickName().equals(userInfo.getNickName())&&dbInfo.getCurrentCoinCount()<Constants.UPDATE_NICK_NAME_COIN){
			throw new BusinessException("硬币不足,无法改名");
		}

		if(!dbInfo.getNickName().equals(userInfo.getNickName())){
			Integer count = this.infoMapper.updateCoinCountInfo(userInfo.getUserId(),-Constants.UPDATE_NICK_NAME_COIN);
			if(count==0){
				throw new BusinessException("硬币不足，无法改名");
			}
		}

		this.infoMapper.updateByUserId(userInfo,userInfo.getUserId());

		Boolean updateTokenInfo = false;
		if(!userInfo.getAvatar().equals(tokenUserInfoDto.getAvatar())){
			tokenUserInfoDto.setAvatar(userInfo.getAvatar());
			updateTokenInfo = true;
		}
		if(!tokenUserInfoDto.getNickName().equals(userInfo.getNickName())){
			tokenUserInfoDto.setNickName(userInfo.getNickName());
			updateTokenInfo = true;
		}
		if(updateTokenInfo){
			redisComponent.updateTokenInfo(tokenUserInfoDto);
		}

	}

	@Override
	public UserCountInfoDto getUserCountInfo(String userId) {
		Info userInfo = getInfoByUserId(userId);
		Integer fansCount = userFocusMapper.selectFansCount(userId);
		Integer focusCount = userFocusMapper.selectFocusCount(userId);
		UserCountInfoDto countInfoDto = new UserCountInfoDto();
		countInfoDto.setFansCount(fansCount);
		countInfoDto.setFocusCount(focusCount);
		countInfoDto.setCurrentCoinCount(userInfo.getCurrentCoinCount());
		return countInfoDto;
	}

	@Override
	public void changeUserStatus(String userId, Integer status) {
		Info userInfo = new Info();
		userInfo.setStatus(status);
		this.infoMapper.updateByUserId(userInfo,userId);
	}
}