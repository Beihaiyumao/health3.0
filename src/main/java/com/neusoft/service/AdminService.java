package com.neusoft.service;


import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.neusoft.DataDictionary.NoticeState;
import com.neusoft.DataDictionary.Role;
import com.neusoft.dao.DoctorMapper;
import com.neusoft.dao.UserMapper;
import com.neusoft.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neusoft.dao.AdminMapper;
import com.neusoft.tool.SystemTool;

@Service
public class AdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DoctorMapper doctorMapper;

    //错误信息
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 管理员登陆
     *
     * @param adminLogin
     * @return
     */
    public boolean adminLogin(Admin adminLogin) {
        boolean adminLoginState = true;
        Admin admin = adminMapper.adminLogin(adminLogin.getUsername());
        if (admin == null) {
            errorMessage = "用户名错误";
            adminLoginState = false;
        } else if (!admin.getPassword().equals(adminLogin.getPassword())) {
            errorMessage = "密码错误";
            adminLoginState = false;
        }
        return adminLoginState;
    }

    /**
     * 新增管理员
     *
     * @param addAdmin
     */
    public Result addAdmin(Admin addAdmin) {
        //管理员id
        String adminId = SystemTool.uuid();
        addAdmin.setAdminId(adminId);
        addAdmin.setCreateTime(SystemTool.getDateTime());
        if (adminMapper.selectAdminByName(addAdmin.getUsername()).size() == 0) {
            return (SystemTool.insert(adminMapper.addAdmin(addAdmin)));
        } else {
            return new Result(200, "该用户名已被注册,请重试!", false);
        }
    }

    /**
     * 删除管理员
     *
     * @param adminId
     */
    public Result deleteAdmin(String adminId) {
        return SystemTool.delete(adminMapper.deleteAdmin(adminId));
    }

    /**
     * 未删除普通用户列表
     *
     * @return
     */
    public Page<User> selectAllUser(Integer pageNum, Integer pageSize) {
        //用插件进行分页
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectAllUser();
    }

    /**
     * 已删除普通用户列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<User> selectAllDeleteUser(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectAllDeleteUser();
    }

    /**
     * 待审核文章列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<HealthyArticle> selectExamineHelthyArticle(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectExamineHelthyArticle();
    }

    /**
     * 审核通过文章列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<HealthyArticle> selectPassHelthyArticle(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectPassHelthyArticle();
    }

    /**
     * 审核不通过列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<HealthyArticle> selectOutHelthyArticle(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectOutHelthyArticle();
    }

    /**
     * 管理员列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<Admin> selectAllAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectAllAdmin();
    }

    /**
     * 拉黑普通用户
     *
     * @param userId
     * @param msg
     * @return
     */
    public Result deleteUserById(String userId, String msg) {
        Blacklist deleteUser = new Blacklist();
        deleteUser.setBlacklistId(SystemTool.uuid());
        deleteUser.setCreateTime(SystemTool.getDateTime());
        User useInfo = userMapper.selectUserInfoByUserId(userId);
        deleteUser.setEmail(useInfo.getEmail());
        deleteUser.setUserId(userId);
        deleteUser.setMsg(msg);
        deleteUser.setRole(Role.USER.getCode());
        int code = adminMapper.deleteUserById(deleteUser);
        if (code == 1) {
            //更新用户表字段
            adminMapper.updateUserBlackState(userId);
            return new Result(100, "已成功拉黑该用户", true);
        } else {
            return new Result(200, "拉黑失败", false);
        }
    }

    /**
     * 拉黑医生用户
     *
     * @param doctorId
     * @param msg
     * @return
     */
    public Result deleteDoctorInfoById(String doctorId, String msg) {
        Blacklist deleteDoctor = new Blacklist();
        deleteDoctor.setBlacklistId(SystemTool.uuid());
        deleteDoctor.setCreateTime(SystemTool.getDateTime());
        Doctor doctorInfo = doctorMapper.selectDoctorInfoById(doctorId);
        deleteDoctor.setMsg(msg);
        deleteDoctor.setUserId(doctorId);
        deleteDoctor.setEmail(doctorInfo.getEmail());
        deleteDoctor.setRole(Role.DOCTOR.getCode());
        int code = adminMapper.deleteUserById(deleteDoctor);
        if (code == 1) {
            //更新医生表字段
            adminMapper.updateDoctorBlackState(doctorId);
            return new Result(100, "已成功拉黑该医生", true);
        } else {
            return new Result(200, "拉黑失败", false);
        }
    }

    /**
     * 未删除医生列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<Doctor> selectAllDoctor(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectAllDoctor();
    }

    /**
     * 已删除的医生列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<Doctor> selectAllDeleteDoctor(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectAllDeleteDoctor();
    }

    /**
     * 所有问题列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<Question> selseAllQuestion(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selseAllQuestion();
    }

    /**
     * 所有问题回答列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<AnswerQuestion> selectAllAnswerQuestion(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectAllAnswerQuestion();
    }

    /**
     * 查询所有文章评论
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<ArticleComment> selectAllArticleComment(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectAllArticleComment();
    }

    /**
     * 查询所有评论的回复
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<CommentReply> selectAllCommentReply(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectAllCommentReply();
    }

    /**
     * 新增导航栏
     *
     * @param navBar
     * @return
     */
    public Result insertNavBar(NavBar navBar) {
        navBar.setCreateTime(SystemTool.getDateTime());
        navBar.setNavBarId(SystemTool.uuid());
        return SystemTool.insert(adminMapper.insertNavBar(navBar));
    }

    /**
     * 导航栏
     *
     * @return
     */
    public List<NavBar> selectNavBar() {
        return adminMapper.selectNavBar();
    }

    /**
     * 删除导航栏
     *
     * @param navBarId
     * @return
     */
    public Result deleteNavBar(String navBarId) {
        int code = adminMapper.deleteNavBar(navBarId);
        return SystemTool.delete(code);
    }

    /**
     * 新增一级菜单
     *
     * @param firstMenu
     * @return
     */
    public Result insertFirstMenu(FirstMenu firstMenu) {
        firstMenu.setCreateTime(SystemTool.getDateTime());
        firstMenu.setFirstMenuId(SystemTool.uuid());
        return SystemTool.insert(adminMapper.insertFirstMenu(firstMenu));
    }

    /**
     * 所有一级菜单
     *
     * @return
     */
    public Page<FirstMenu> selectFirstMenu(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectFirstMenu();
    }

    /**
     * 导航栏下的一级菜单
     *
     * @param navBarId
     * @return
     */
    public List<FirstMenu> selectFirstMenuByNavBarId(String navBarId) {
        return adminMapper.selectFirstMenuByNavBarId(navBarId);
    }

    /**
     * 删除一级菜单
     *
     * @param firstMenuId
     * @return
     */
    public Result deleteFirstMenu(String firstMenuId) {
        return SystemTool.delete(adminMapper.deleteFirstMenu(firstMenuId));
    }

    /**
     * 新增用户反馈
     *
     * @param userFeedBack
     * @return
     */
    public Result insertFeedback(UserFeedBack userFeedBack) {
        userFeedBack.setCreateTime(SystemTool.getDateTime());
        userFeedBack.setUserFeedbackId(SystemTool.uuid());
        return SystemTool.insert(adminMapper.insertFeedback(userFeedBack));
    }

    /**
     * 所有用户的反馈
     *
     * @param pageNum
     * @param pageSize
     * @param msg
     * @return
     */
    public Page<UserFeedBack> selectFeedback(Integer pageNum, Integer pageSize, String msg) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectFeedback(msg);
    }

    /**
     * 删除用户反馈
     *
     * @param userFeedbackId
     * @return
     */
    public Result deleteFeedback(String userFeedbackId) {
        return SystemTool.delete(adminMapper.deleteFeedback(userFeedbackId));
    }

    /**
     * 取消拉黑用户
     *
     * @param userId
     * @return
     */
    public Result cancelBlackUser(String userId) {
        int code = adminMapper.cancelBlackUser(userId);
        if (code == 1) {
            return new Result(100, "取消拉黑成功!", true);
        } else {
            return new Result(200, "取消拉黑失败,请重试", false);
        }
    }

    /**
     * 取消拉黑医生
     *
     * @param doctorId
     * @return
     */
    public Result cancelBlackDoct(String doctorId) {
        int code = adminMapper.cancelBlackDoct(doctorId);
        if (code == 1) {
            return new Result(100, "取消拉黑成功!", true);
        } else {
            return new Result(200, "取消拉黑失败,请重试", false);
        }
    }

    /**
     * 删除评论
     *
     * @param commentId
     * @return
     */
    public Result deleteCommentById(String commentId) {
        return SystemTool.delete(adminMapper.deleteCommentById(commentId));
    }

    /**
     * 删除回复
     *
     * @param replyId
     * @return
     */
    public Result deleteReplyById(String replyId) {
        return SystemTool.delete(adminMapper.deleteReplyById(replyId));
    }

    /**
     * 修改密码
     *
     * @param password
     * @param adminId
     * @param opassword
     * @return
     */
    public Result changePassword(String adminId, String password, String opassword) {
        Admin admin = adminMapper.selectAdminById(adminId);
        if (!admin.getPassword().equals(opassword)) {
            return new Result(200, "旧密码错误,请重试!", false);
        } else {
            int code = adminMapper.changePassword(password, adminId);
            if (code == 1) {
                return new Result(100, "修改密码成功", true);
            } else {
                return new Result(200, "修改密码失败,请重试!", false);
            }
        }
    }

    /**
     * 新增文章类别
     *
     * @param healthArticleGenre
     * @return
     */
    public Result insertHealthArticleGenre(HealthArticleGenre healthArticleGenre) {
        HealthArticleGenre healthArticleGenre1 = adminMapper.selectHealthArticleGenreByCodeOrName(healthArticleGenre.getArticleGenreCode(), healthArticleGenre.getArticleGenreName());
        if (healthArticleGenre1 != null) {
            return new Result(200, "已存在该分类的code或name", false);
        } else {
            healthArticleGenre.setCreateTime(SystemTool.getDateTime());
            healthArticleGenre.setHealthyArticleGenreId(SystemTool.uuid());
            return SystemTool.insert(adminMapper.insertHealthArticleGenre(healthArticleGenre));
        }

    }

    /**
     * 所有的文章类别
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<HealthArticleGenre> selectAllArticleGenre(Integer pageNum, Integer pageSize, String all) {
        PageHelper.startPage(pageNum, pageSize);
        if (all.equals("100")) {
            return adminMapper.selectAllArticleGenre();
        } else {
            return adminMapper.selectArticleGenreOutAll();
        }

    }

    /**
     * 删除文章分类
     *
     * @param healthyArticleGenreId
     * @return
     */
    public Result deleteHealthArticleGenreById(String healthyArticleGenreId) {
        return SystemTool.delete(adminMapper.deleteHealthArticleGenreById(healthyArticleGenreId));
    }

    /**
     * 新增问题分类
     *
     * @param healthQuestionGenre
     * @return
     */
    public Result insertHealthQuestionGenre(HealthQuestionGenre healthQuestionGenre) {
        HealthQuestionGenre healthQuestionGenre1 = adminMapper.selectHealthQuestionGenreByCodeOrName(healthQuestionGenre.getQuestionGenreCode(), healthQuestionGenre.getQuestionGenreName());
        if (healthQuestionGenre1 != null) {
            return new Result(200, "已存在该分类的code或name", false);
        } else {
            healthQuestionGenre.setCreateTime(SystemTool.getDateTime());
            healthQuestionGenre.setHealthyQuestionGenreId(SystemTool.uuid());
            return SystemTool.insert(adminMapper.insertHealthQuestionGenre(healthQuestionGenre));
        }
    }

    /**
     * 所有问题分类
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<HealthQuestionGenre> selectAllHealthQuestionGenre(Integer pageNum, Integer pageSize, String all) {
        PageHelper.startPage(pageNum, pageSize);
        if (all.equals("100")) {
            //包括全部
            return adminMapper.selectAllHealthQuestionGenre();
        } else {
            return adminMapper.selectHealthQuestionGenreOutAll();
        }

    }

    /**
     * 删除问题分类
     *
     * @param healthyQuestionGenreId
     * @return
     */
    public Result deleteHealthQuestionGenreById(String healthyQuestionGenreId) {
        return SystemTool.delete(adminMapper.deleteHealthQuestionGenreById(healthyQuestionGenreId));
    }

    /**
     * 修改文章分类名称
     *
     * @param healthyArticleGenreId
     * @param articleGenreName
     * @return
     */
    public Result updateHealthArticleGenre(String healthyArticleGenreId, String articleGenreName) {
        return SystemTool.update(adminMapper.updateHealthArticleGenre(healthyArticleGenreId, articleGenreName));
    }

    /**
     * 修改问题分类名称
     *
     * @param healthyQuestionGenreId
     * @param questionGenreName
     * @return
     */
    public Result updateHealthQuestionGenre(String healthyQuestionGenreId, String questionGenreName) {
        return SystemTool.update(adminMapper.updateHealthQuestionGenre(healthyQuestionGenreId, questionGenreName));
    }

    /**
     * 新增用户通知栏
     *
     * @param noticeMsg
     * @return
     */
    public Result insertNoticeMsg(NoticeMsg noticeMsg) {
        noticeMsg.setCreateTime(SystemTool.getDateTime());
        noticeMsg.setNoticeMsgId(SystemTool.uuid());
        noticeMsg.setState(NoticeState.NOT_STATE.getCode());
        return SystemTool.insert(adminMapper.insertNoticeMsg(noticeMsg));
    }

    /**
     * 所有用户通知
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<NoticeMsg> selectAllNoticeMsg(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return adminMapper.selectAllNoticeMsg();
    }

    /**
     * 开启通知
     *
     * @param state
     * @param noticeMsgId
     * @return
     */
    public Result updateNoticeState(String state, String noticeMsgId) {
        //查询是否存在已开启的通知
        List<String> list = adminMapper.selectAllNoticeStateIsYes();
        if (list.size() != 0) {
            return new Result(300, "已存在开启的通知", true);
        } else {
            int code = adminMapper.updateNoticeState(state, noticeMsgId);
            if (code == 1) {
                return new Result(100, "开启通知成功", true);
            } else {
                return new Result(200, "开启通知失败", false);
            }
        }

    }

    /**
     * 关闭通知
     *
     * @param state
     * @param noticeMsgId
     * @return
     */
    public Result updateNoticeStateIsFalse(String state, String noticeMsgId) {
        int code = adminMapper.updateNoticeState(state, noticeMsgId);
        if (code == 1) {
            return new Result(100, "关闭通知成功", true);
        } else {
            return new Result(200, "关闭通知失败", false);
        }
    }

    /**
     * 删除通知
     *
     * @param noticeMsgId
     * @return
     */
    public Result deleteNoticeStateById(String noticeMsgId) {
        return SystemTool.delete(adminMapper.deleteNoticeStateById(noticeMsgId));
    }

    /**
     * 查询正在通知的消息
     *
     * @return
     */
    public Result selectIsYesNotice() {
        List<NoticeMsg> list = adminMapper.selectIsYesNotice();
        if (list.size() != 0) {
            return new Result(100, "成功", true, list.get(0));
        } else {
            return new Result(200, "没有正在通知的消息", false);
        }

    }
}
