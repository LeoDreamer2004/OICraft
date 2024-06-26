package org.dindier.oicraft.util.web;

import org.dindier.oicraft.model.Problem;
import org.dindier.oicraft.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * A utility class to web problems from OpenJudge
 *
 * @author LeoDreamer
 */
public class CrawlToOpenJudge {

    public static void main(String[] args) {
        CrawlToOpenJudge crawlToOpenJudge = new CrawlToOpenJudge();
        System.out.println(crawlToOpenJudge.crawl(null));
    }

    /**
     * Crawl a problem from OpenJudge
     *
     * @param url    The url of the problem
     * @param author The author of the problem
     * @return The problem crawled from OpenJudge
     */
    public static Problem crawlProblem(String url, User author) {
        String content = WebUtil.getContentFromUrl(url, "UTF-8");
        String title = WebUtil.findPattern(content, "<div id=\"pageTitle\"><h2>(.*?)</h2></div>", 1);
        String description = WebUtil.findPattern(content,
                "<dt>描述</dt>\\s*?<dd>(.*?)</dd>", 1);
        String inputFormat = WebUtil.findPattern(content,
                "<dt>输入</dt>\\s*?<dd>(.*?)</dd>", 1);
        String outputFormat = WebUtil.findPattern(content,
                "<dt>输出</dt>\\s*?<dd>(.*?)</dd>", 1);
        String timeLimitStr = WebUtil.findPattern(content,
                "<dt>总时间限制: </dt>\\s*?<dd>(\\d*?)ms</dd>", 1);
        int timeLimit = Integer.parseInt(Objects.requireNonNull(timeLimitStr));
        String memoryLimitStr = WebUtil.findPattern(content,
                "<dt>内存限制: </dt>\\s*?<dd>(\\d*?)kB</dd>", 1);
        int memoryLimit = Integer.parseInt(Objects.requireNonNull(memoryLimitStr));
        return new Problem(author, title, description, inputFormat, outputFormat,
                Problem.Difficulty.EASY, timeLimit, memoryLimit);
    }

    /**
     * Crawl the problems from OpenJudge <a href="http://bailian.openjudge.cn">百练题目组</a>
     *
     * @param author The author of the problems
     * @return The problems crawled from OpenJudge
     */
    public List<Problem> crawl(User author) {
        String url = "http://bailian.openjudge.cn";
        String content = WebUtil.getContentFromUrl(url + "/practice", "UTF-8");
        WebUtil.saveToFile(content, "temp.html");
        String pattern = "<td class=\"title\"><a href=\"(.*?)\">(.*?)</a></td>";
        List<String> problems = WebUtil.findAllPatterns(content, pattern, 1);
        List<String> valid = new HashSet<>(problems).stream(). // remove duplicates
                filter(u -> !u.endsWith("/statistics/")).toList();
        return valid.stream().sorted().map(u -> crawlProblem(url + u, author)).toList();
    }
}
