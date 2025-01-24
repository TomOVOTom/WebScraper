package webscraper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import webscraper.model.BilibiliVideo;

@Slf4j
@Service
public class BilibiliScraperService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public BilibiliVideo scrapeVideoInfo(String bvid) {
        try {
            // 使用B站官方API获取视频信息
            String videoApiUrl = "https://api.bilibili.com/x/web-interface/view?bvid=" + bvid;
            String videoResponse = Jsoup.connect(videoApiUrl)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .ignoreContentType(true)
                    .execute()
                    .body();

            JsonNode videoData = objectMapper.readTree(videoResponse).get("data");
            
            BilibiliVideo video = new BilibiliVideo();
            video.setBvid(bvid);
            video.setTitle(videoData.get("title").asText());
            video.setDescription(videoData.get("desc").asText());

            // UP主信息
            JsonNode owner = videoData.get("owner");
            String mid = owner.get("mid").asText();
            video.setUploader(owner.get("name").asText());
            video.setUploaderAvatar(owner.get("face").asText());

            // 获取UP主粉丝数
            String userApiUrl = "https://api.bilibili.com/x/web-interface/card?mid=" + mid;
            String userResponse = Jsoup.connect(userApiUrl)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .ignoreContentType(true)
                    .execute()
                    .body();

            JsonNode userData = objectMapper.readTree(userResponse).get("data").get("card");
            video.setUploaderFans(formatNumber(userData.get("fans").asLong()) + " 粉丝");

            // 统计数据
            JsonNode stat = videoData.get("stat");
            video.setViewCount(formatNumber(stat.get("view").asLong()));
            video.setLikeCount(formatNumber(stat.get("like").asLong()));
            video.setCoinCount(formatNumber(stat.get("coin").asLong()));
            video.setFavoriteCount(formatNumber(stat.get("favorite").asLong()));
            video.setShareCount(formatNumber(stat.get("share").asLong()));
            video.setDanmakuCount(formatNumber(stat.get("danmaku").asLong()));

            // 上传时间
            long timestamp = videoData.get("pubdate").asLong();
            video.setUploadTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new java.util.Date(timestamp * 1000)));

            // 标签
            StringBuilder tags = new StringBuilder();
            if (videoData.has("tag")) {
                videoData.get("tag").forEach(tag -> {
                    if (tags.length() > 0) {
                        tags.append(",");
                    }
                    tags.append(tag.get("tag_name").asText());
                });
            }
            video.setTags(tags.toString());

            return video;
        } catch (Exception e) {
            log.error("爬取视频信息失败: " + e.getMessage(), e);
            throw new RuntimeException("爬取视频信息失败", e);
        }
    }

    private String formatNumber(long number) {
        if (number < 10000) {
            return String.valueOf(number);
        } else if (number < 100000000) {
            return String.format("%.1f万", number / 10000.0);
        } else {
            return String.format("%.1f亿", number / 100000000.0);
        }
    }
}