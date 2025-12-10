package com.easylive.service;

import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.dto.UserCountInfoDto;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.po.Info;
import com.easylive.entity.query.InfoQuery;

import java.util.List;
/**
 * @Description用户信息Service
 * @date:2025/09/28
 * @author:magnico
 */
public interface InfoService{

	/**
	 * 根据条件查询列表
	 */
	List<Info> findListByParam(InfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(InfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Info> findListByPage(InfoQuery query);

	/**
	 * 新增
	 */
	Integer add(Info bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<Info> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<Info> listBean);

	/**
	 * 根据UserId查询
	 */
	Info getInfoByUserId(String userId);

	/**
	 * 根据UserId更新
	 */
	Integer updateInfoByUserId(Info bean, String userId);

	/**
	 * 根据UserId删除
	 */
	Integer deleteInfoByUserId(String userId);

	/**
	 * 根据Email查询
	 */
	Info getInfoByEmail(String email);

	/**
	 * 根据Email更新
	 */
	Integer updateInfoByEmail(Info bean, String email);

	/**
	 * 根据Email删除
	 */
	Integer deleteInfoByEmail(String email);

	/**
	 * 根据NickName查询
	 */
	Info getInfoByNickName(String nickName);

	/**
	 * 根据NickName更新
	 */
	Integer updateInfoByNickName(Info bean, String nickName);

	/**
	 * 根据NickName删除
	 */
	Integer deleteInfoByNickName(String nickName);

	void register(String email,String nickName,String registerPassword);
	TokenUserInfoDto login(String email, String password, String ip);

	Info getUserDetailInfo(String currentUserId,String userId);

	void updateUserInfo(Info userInfo,TokenUserInfoDto tokenUserInfoDto);

	UserCountInfoDto getUserCountInfo(String userId);

	void changeUserStatus(String userId,Integer status);
}