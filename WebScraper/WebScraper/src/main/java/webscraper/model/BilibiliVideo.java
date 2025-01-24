package webscraper.model;

import lombok.Data;

@Data
public class BilibiliVideo {
    private String title;           // 视频标题
    private String bvid;            // BV号
    private String description;     // 视频描述
    private String uploader;        // UP主名称
    private String uploadTime;      // 上传时间
    private String viewCount;       // 观看次数
    private String likeCount;       // 点赞数
    private String coinCount;       // 投币数
    private String favoriteCount;   // 收藏数
    private String shareCount;      // 分享数
    private String danmakuCount;    // 弹幕数
    private String uploaderAvatar;  // UP主头像
    private String uploaderFans;    // UP主粉丝数
    private String tags;            // 视频标签
} 