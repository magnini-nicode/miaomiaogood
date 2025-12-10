package com.easylive.web.controller;

import com.easylive.component.RedisComponent;
import com.easylive.entity.constants.Constants;
import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.dto.UserCountInfoDto;
import com.easylive.entity.po.Info;
import com.easylive.entity.query.InfoQuery;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.exception.BusinessException;
import com.easylive.redis.RedisUtils;
import com.easylive.service.InfoService;
import com.easylive.service.impl.InfoServiceImpl;
import com.easylive.utils.StringTools;
import com.wf.captcha.ArithmeticCaptcha;
import org.apache.ibatis.annotations.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description用户信息Controller
 * @date:2025/09/28
 * @author:magnico
 */
@RestController
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController {

	@Resource
	private InfoService infoService;
	@Resource
	private RedisComponent redisComponent;
    @RequestMapping("/checkCode")
	public ResponseVO checkCode(){
		ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
		String code = captcha.text();
		String checkCodeKey = redisComponent.saveCheckCode(code);
		String checkCodeBase64 = captcha.toBase64();

		Map<String,String> result  = new HashMap<>();
		result.put("checkCode",checkCodeBase64);
		result.put("checkCodeKey",checkCodeKey);
		return getSuccessResponseVO(result);
	}
	@RequestMapping("/register")
	public ResponseVO register(@NotEmpty @Email @Size(max = 150) String email,
							   @NotEmpty @Size(max = 20) String nickName,
							   @NotEmpty @Pattern(regexp = Constants.REGEX_PASSWORD) String registerPassword,
							   @NotEmpty String checkCodeKey,
							   @NotEmpty String checkCode) {
	try{
		if(!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))){
			throw new BusinessException("图片验证码不正确");
		}
		 infoService.register(email,nickName,registerPassword);
		return getSuccessResponseVO(null);
	} finally{
		redisComponent.cleanCheckCode(checkCodeKey);
	}

	}
	@RequestMapping("/login")
	public ResponseVO login(HttpServletRequest request, HttpServletResponse response,
							 @NotEmpty @Email String email,
							 @NotEmpty String password,
							 @NotEmpty String checkCodeKey,
							 @NotEmpty String checkCode) {
		try{
			if(!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))){
				throw new BusinessException("图片验证码不正确");
			}
			String ip = getIpAddr();
			TokenUserInfoDto tokenUserInfoDto = infoService.login(email,password,ip);
			saveToken2Cookie(response, tokenUserInfoDto.getToken());

			return getSuccessResponseVO(tokenUserInfoDto);
		} finally{
			redisComponent.cleanCheckCode(checkCodeKey);

			//可以注释掉
			Cookie[] cookies = request.getCookies();
			 if(cookies!=null){
				 String token = null ;
				 for(Cookie cookie : cookies){
					 if(cookie.getName().equals("token")){
						 token = cookie.getValue();
					   }
				 }

				 if(!StringTools.isEmpty(token)){
					 redisComponent.cleanToken(token);
				 }
			   }
		}

	}
	@RequestMapping("/autoLogin")
	public ResponseVO autoLogin(HttpServletResponse response) {
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
		if(tokenUserInfoDto == null){
			return getSuccessResponseVO(null);
		}
		if(tokenUserInfoDto.getExpireAt()-System.currentTimeMillis()<Constants.REDIS_KEY_EXPIRES_ONE_DAY){
			redisComponent.saveTokenInfo(tokenUserInfoDto);
			saveToken2Cookie(response, tokenUserInfoDto.getToken());
		}
		return getSuccessResponseVO(tokenUserInfoDto);
	}

	@RequestMapping("/logout")
	public ResponseVO logout(HttpServletResponse response) {
		cleanCookie(response);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/getUserCountInfo")
	public ResponseVO getUserCountInfo() {
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
		UserCountInfoDto userCountInfoDto = infoService.getUserCountInfo(tokenUserInfoDto.getUserId());
		return getSuccessResponseVO(userCountInfoDto);
	}
}