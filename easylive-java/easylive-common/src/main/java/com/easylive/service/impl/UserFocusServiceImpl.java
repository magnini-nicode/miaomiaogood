package com.easylive.service.impl;

import com.easylive.entity.po.Info;
import com.easylive.entity.po.UserFocus;
import com.easylive.entity.query.InfoQuery;
import com.easylive.entity.query.UserFocusQuery;
import com.easylive.entity.query.SimplePage;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.PageSize;
import com.easylive.enums.ResponseCodeEnum;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.InfoMapper;
import com.easylive.mappers.UserFocusMapper;
import com.easylive.service.UserFocusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @DescriptionService
 * @date:2025/10/23
 * @author:magnico
 */
@Service("userFocusService")
public class UserFocusServiceImpl implements UserFocusService {

	@Resource
	private UserFocusMapper<UserFocus, UserFocusQuery> userFocusMapper;
    @Resource
    private InfoMapper<Info, InfoQuery> infoMapper;


	/**
	 * 根据条件查询列表
	 */
	public List<UserFocus> findListByParam(UserFocusQuery query) {
		return this.userFocusMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserFocusQuery query) {
		return this.userFocusMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserFocus> findListByPage(UserFocusQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<UserFocus> list = this.findListByParam(query);
		PaginationResultVO<UserFocus> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(),list);		
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserFocus bean) {
		return this.userFocusMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserFocus> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.userFocusMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserFocus> listBean) {
		if (listBean == null || listBean.isEmpty())  {
			return 0;
		}
		return this.userFocusMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据UserIdAnd, FocusUserId查询
	 */
	public UserFocus getUserFocusByUserIdAndFocusUserId(String userId,String focusUserId) {
		return this.userFocusMapper.selectByUserIdAndFocusUserId(userId, focusUserId);
	}

	/**
	 * 根据UserIdAnd, FocusUserId更新
	 */
	public Integer updateUserFocusByUserIdAndFocusUserId(UserFocus bean, String userId,String focusUserId) {
		return this.userFocusMapper.updateByUserIdAndFocusUserId(bean,userId, focusUserId);
	}

	/**
	 * 根据UserIdAnd, FocusUserId删除
	 */
	public Integer deleteUserFocusByUserIdAndFocusUserId(String userId,String focusUserId) {
		return this.userFocusMapper.deleteByUserIdAndFocusUserId(userId, focusUserId);
	}

	@Override
	public void focusUser(String userId,String focusUserId) {
		if(userId.equals(focusUserId)){
			throw new BusinessException("不能对自己进行此操作");
		}

		UserFocus dbInfo = this.userFocusMapper.selectByUserIdAndFocusUserId(userId,focusUserId);
		if(dbInfo!=null){
			return;
		}
		Info userInfo = infoMapper.selectByUserId(focusUserId);
		if(userInfo==null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserFocus focus = new UserFocus();
		focus.setUserId(userId);
		focus.setFocusUserId(focusUserId);
		focus.setFocusTime(new Date());
		this.userFocusMapper.insert(focus);

	}

	@Override
	public void cancelFocus(String userId,String focusUserId) {
       this.userFocusMapper.deleteByUserIdAndFocusUserId(userId,focusUserId);

	}


}