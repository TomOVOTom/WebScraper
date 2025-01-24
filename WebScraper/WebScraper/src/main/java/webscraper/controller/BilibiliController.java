package webscraper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webscraper.model.BilibiliVideo;
import webscraper.service.BilibiliScraperService;

@RestController
@RequestMapping("/api/bilibili")
public class BilibiliController {

    @Autowired
    private BilibiliScraperService scraperService;

    @GetMapping("/video/{bvid}")
    public BilibiliVideo getVideoInfo(@PathVariable String bvid) {
        return scraperService.scrapeVideoInfo(bvid);
    }
} 