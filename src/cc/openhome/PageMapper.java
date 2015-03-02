package cc.openhome;

import static cc.openhome.IO.*;
import static java.lang.System.out;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Justin
 */
public class PageMapper {
    private static PathContent template = pathContent(Paths.get("c:\\workspace\\template.html"));
    
    private static Map<String, Pattern> patterns = new HashMap<>();
    static {
        patterns.put("title", Pattern.compile("<title>(.*)</title>"));
        patterns.put("div class=\"article\"", Pattern.compile("<div class=\"article\">((.*\\s*)*?)</div>"));
        patterns.put("stripTags", Pattern.compile("\\<[^>]*>"));
        patterns.put("img", Pattern.compile("<img (.+?)>"));
        patterns.put("table", Pattern.compile("<table class=\"cmd\">((.*\\s*)*?)</table>"));
        patterns.put("tr", Pattern.compile("<table class=\"cmd\">\\s*<tbody>\\s*<tr>((.*\\s*)*?)</tr>\\s*</tbody>\\s*</table>"));
        patterns.put("td", Pattern.compile("<td>((.*\\s*)*?)</td>"));
    }    
    
    private static String rwdImg =
                  "<div class=\"pure-g\">" 
                +     "<div class=\"pure-u-1\">" 
                +         "<img class=\"pure-img-responsive\" $1 />"
                +     "</div>"
                + "</div>";
            
    public static void main(String[] args) {
        List<String> htmlFiles = htmlFiles(Paths.get("c:\\workspace\\NewJava\\"));
        htmlFiles.stream()
                .map(Paths::get)
                // 排除首頁，因為比較複雜，要手動修改
                .filter(path -> !path.getFileName().toString().equals("index.html"))
                .map(IO::pathContent)
                // 處理 div class="article" 與 title
                .map(PageMapper::map2Template)
                .map(PageMapper::img2RWD)
                .map(PageMapper::cmdTable2Div)
                .forEach(IO::write);
        
        
    }
    
    private static String tagContent(String content, String tag) {
        Matcher matcher = patterns.get(tag).matcher(content);
        matcher.find();
        return matcher.group(1);
    }
    
    private static PathContent map2Template(PathContent pathContent) {
        String content = tagContent(pathContent.content, "div class=\"article\"");
         pathContent.content = 
             template.content
                   .replace("#content#", Matcher.quoteReplacement(content))
                   .replaceAll("#title#", tagContent(pathContent.content, "title"))
                   .replaceAll("#fileName#", pathContent.path.getFileName().toString())
                   .replaceAll("#description#", patterns.get("stripTags").matcher(content).replaceAll("").trim().substring(0, 100) + "...");
         return pathContent;
    }
    
    private static PathContent img2RWD(PathContent pathContent) {
        pathContent.content = patterns.get("img").matcher(pathContent.content).replaceAll(rwdImg);
        return pathContent;
    }
    
    private static PathContent cmdTable2Div(PathContent pathContent) {
        String cmdContent = pathContent.content;
        Matcher matcher = patterns.get("tr").matcher(cmdContent);
        if(matcher.find()) {
            cmdContent = matcher.group(1);
            cmdContent = tagContent(cmdContent, "td").trim();
            pathContent.content = patterns.get("table").matcher(pathContent.content)
                  .replaceAll(Matcher.quoteReplacement("<div class=\"cmd\">" + cmdContent + "</div>"));
        }
        return pathContent;
    }
}
