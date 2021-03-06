package com.neusoft.entity;

import com.neusoft.DataDictionary.LikedStatusEnum;

import java.util.Date;

/**
 * 点赞
 * 2019/3/12
 */
public class LikeArticle {
    private String likeArticleId; //id

    private String userId;//用户id

    private String articleId;//文章id

    private Date createTime; //创建时间
    //点赞的状态.默认未点赞
    private Integer status = LikedStatusEnum.UNLIKE.getCode();

    public LikeArticle(String userId, String articleId, Integer status) {
        this.userId = userId;
        this.articleId = articleId;
        this.status = status;
    }

    public String getLikeArticleId() {
        return likeArticleId;
    }

    public void setLikeArticleId(String likeArticleId) {
        this.likeArticleId = likeArticleId == null ? null : likeArticleId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId == null ? null : articleId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}