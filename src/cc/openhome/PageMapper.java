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
    
    public static Map<String, Pattern> patterns = new HashMap<>();
    static {
        patterns.put("title", Pattern.compile("<title>(.+?)</title>", Pattern.DOTALL));
        patterns.put("div class=\"article\"", Pattern.compile("<div class=\"article\">((.*\\s*)*?).*</div>"));
        patterns.put("all", Pattern.compile("\\<[^>]*>"));
        patterns.put("img", Pattern.compile("<img (.+?)>", Pattern.DOTALL));
        patterns.put("table", Pattern.compile("<table class=\"cmd\">.+?<tr>.+?<td>(.+?)</td>.+?</tr>.+?</table>", Pattern.DOTALL));
        patterns.put("span class=\"courier\"", Pattern.compile("<span.*?class=\"courier\">(.*?)</span>", Pattern.DOTALL));
        patterns.put("div class=\"aside\"", Pattern.compile("<div class=\"aside\">((.*\\s*)*?)</div>"));
        patterns.put("前情", Pattern.compile("<a href.*?>.*?前情</a>"));
        patterns.put("後續", Pattern.compile("<a href.*?>後續.*?</a>"));
        patterns.put("tdStudyGossip", Pattern.compile("<td style=\"vertical-align: top; width: \\d*px; text-align: left;\">(.*?)</td>", Pattern.DOTALL));
        patterns.put("tdAlgorithmGossip", Pattern.compile("<td style=\"width: \\d*px; vertical-align: top;\">(.*?)</td>", Pattern.DOTALL));
    }    
    
    private static String rwdImg =
                  "<div class=\"pure-g\">" 
                +     "<div class=\"pure-u-1\">" 
                +         "<img class=\"pure-img-responsive\" $1 />"
                +     "</div>"
                + "</div>";
            
    public static void main(String[] args) {
        List<String> htmlFiles = htmlFiles(Paths.get(args[0]));
        htmlFiles.stream()
                .map(Paths::get)
                // 排除首頁，因為比較複雜，要手動修改
                .filter(path -> !path.getFileName().toString().equals("index.html"))
                .map(IO::pathContent)
                .map(PageMapper::titleDivArticle2Template)
                .map(PageMapper::img2RWD)
                .map(PageMapper::cmdTable2Div)
                .forEach(IO::write);
    }
    
    private static String tagContent(String content, String tag) {
        Matcher matcher = patterns.get(tag).matcher(content);
        matcher.find();
        return matcher.group(1);
    }
    
    public static PathContent titleDivArticle2Template(PathContent pathContent) {
        String content = tagContent(pathContent.content, "div class=\"article\"");
        pathContent.content = fromTemplate(
                    pathContent.path.getFileName().toString(), 
                    tagContent(pathContent.content, "title"), 
                    content);
         return pathContent;
    }
    
    public static String fromTemplate(String fileName, String title, String content) {
         return template.content
                   .replace("#content#", Matcher.quoteReplacement(content))
                   .replaceAll("#title#", title)
                   .replaceAll("#fileName#", fileName)
                   .replaceAll("#description#", patterns.get("all").matcher(content).replaceAll("").trim().substring(0, 100) + "...");
    }
    
    public static PathContent titleTdArticle2Template(PathContent pathContent, String td) {
        String content = tagContent(pathContent.content, td);
        pathContent.content = fromTemplate(
                    pathContent.path.getFileName().toString(), 
                    tagContent(pathContent.content, "title"), 
                    content);
        
         return pathContent;
    }
    
    public static PathContent img2RWD(PathContent pathContent) {
        return replace(pathContent, "img", rwdImg);
    }
    
    public static PathContent cmdTable2Div(PathContent pathContent) {
        return replace(pathContent, "table", "<div class=\"cmd\">$1</div>");
    }
    
    public static PathContent spanCourier2Code(PathContent pathContent) {
        return replace(pathContent, "span class=\"courier\"", "<code>$1</code>");
    }
    
    public static PathContent pre2PrettyPrint(PathContent pathContent, String lang) {
         pathContent.content = pathContent.content
                 .replaceAll("<pre>", "<pre class=\"prettyprint\"><code lang=\""+ lang + "\">")
                 .replaceAll("</pre>", "</code></pre>");
        return pathContent;
    }
    
    public static PathContent pre2PrettyPrint(PathContent pathContent) {
         pathContent.content = pathContent.content
                 .replaceAll("<pre>", "<pre class=\"prettyprint\"><code>")
                 .replaceAll("</pre>", "</code></pre>");
        return pathContent;
    }
    
    public static PathContent removeDivAside(PathContent pathContent) {
        return replace(pathContent, "div class=\"aside\"", "");
    }
    
    public static PathContent removePreNextLink(PathContent pathContent) {
        return replace(replace(pathContent, "前情", ""), "後續", "");
    }
    
    public static PathContent replace(PathContent pathContent, String name, String replacement) {
        pathContent.content = patterns.get(name).matcher(pathContent.content).replaceAll(replacement);
        return pathContent;
    }
    
    public static PathContent clean(PathContent pathContent) {
        pathContent.content = pathContent.content
                 .replaceAll("<small.*?>", "")
                 .replaceAll("</small>", "")
                 .replaceAll("<big>", "")
                 .replaceAll("</big>", "")
                .replaceAll(" style=\"width: \\d*px; height: \\d*px;\"", "");
        return pathContent;
    }
}
