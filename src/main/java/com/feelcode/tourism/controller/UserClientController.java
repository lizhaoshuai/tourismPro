package com.feelcode.tourism.controller;

import com.feelcode.tourism.base.controller.BaseController;
import com.feelcode.tourism.base.utils.RedisConstants;
import com.feelcode.tourism.base.utils.RedisUtil;
import com.feelcode.tourism.base.utils.StateParameter;
import com.feelcode.tourism.entity.User;
import com.feelcode.tourism.entity.UserRequestPageDTO;
import com.feelcode.tourism.entity.UserResponsePageDTO;
import com.feelcode.tourism.entity.UserSessionEntity;
import com.feelcode.tourism.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Author: 朱利尔
 * @Description:
 * @Date: Created in 21:59 2020/5/7
 * @Modified By:
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value="/c/user")
@Slf4j
public class UserClientController extends BaseController {

    @Resource
    UserService userService;
    @Resource
    RedisUtil redisUtil;

    /**
     * @auther: zhangyingqi
     * @date: 17:37 2020/4/30
     * @param: [request, user]
     * @return: org.springframework.ui.ModelMap
     * @Description: 用户保存&更新
     */
    @RequestMapping(value="/login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ModelMap login(@RequestBody User user,HttpServletRequest req){
        try {
            User us = userService.findByUserNameAndPassword(user.getUserName(),user.getPassword());
            if(us!=null){
                UserSessionEntity userSessionEntity = new UserSessionEntity();
                userSessionEntity.setUserName(us.getUserName());
                us.setUserInfo(userSessionEntity);
                redisUtil.set("user_session_"+us.getId(),us, RedisConstants.datebase1);
                return getModelMap(StateParameter.SUCCESS, us, "登录成功");
            }else {
                return getModelMap(StateParameter.FAULT, null, "登录失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getModelMap(StateParameter.FAULT, null, "登录失败");
        }
    }

    /**
     * @auther: zhangyingqi
     * @date: 17:37 2020/4/30
     * @param: [request, user]
     * @return: org.springframework.ui.ModelMap
     * @Description: 用户保存&更新
     */
    @RequestMapping(value="/getSession", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ModelMap getSession(@RequestBody User user){
        try {
            User us = (User)redisUtil.get("user_session_"+user.getId(),RedisConstants.datebase1);
            if(us!=null){
                UserSessionEntity userSessionEntity = new UserSessionEntity();
                userSessionEntity.setUserName(us.getUserName());
                us.setUserInfo(userSessionEntity);
                return getModelMap(StateParameter.SUCCESS, us, "已登录");
            }else {
                return getModelMap(StateParameter.FAULT, null, "未登录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getModelMap(StateParameter.FAULT, null, "登录态检测失败");
        }
    }

}