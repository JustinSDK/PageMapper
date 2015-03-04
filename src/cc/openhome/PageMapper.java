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
        patterns.put("title", Pattern.compile("<title>(.+?)</title>", Pattern.DOTALL));
        patterns.put("div class=\"article\"", Pattern.compile("<div class=\"article\">((.*\\s*)*?)</div>"));
        patterns.put("all", Pattern.compile("\\<[^>]*>"));
        patterns.put("img", Pattern.compile("<img (.+?)>", Pattern.DOTALL));
        patterns.put("table", Pattern.compile("<table class=\"cmd\">.+?<tr>.+?<td>(.+?)</td>.+?</tr>.+?</table>", Pattern.DOTALL));
        patterns.put("span class=\"courier\"", Pattern.compile("<span.*?class=\"courier\">(.*?)</span>", Pattern.DOTALL));
        patterns.put("div class=\"aside\"", Pattern.compile("<div class=\"aside\">((.*\\s*)*?)</div>"));
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
    
    public static PathContent map2Template(PathContent pathContent) {
        String content = tagContent(pathContent.content, "div class=\"article\"");
        
         pathContent.content = 
             template.content
                   .replace("#content#", Matcher.quoteReplacement(content))
                   .replaceAll("#title#", tagContent(pathContent.content, "title"))
                   .replaceAll("#fileName#", pathContent.path.getFileName().toString())
                   .replaceAll("#description#", patterns.get("all").matcher(content).replaceAll("").trim().substring(0, 100) + "...");
         return pathContent;
    }
    
    public static PathContent img2RWD(PathContent pathContent) {
        pathContent.content = patterns.get("img").matcher(pathContent.content).replaceAll(rwdImg);
        return pathContent;
    }
    
    public static PathContent cmdTable2Div(PathContent pathContent) {
        pathContent.content = patterns.get("table").matcher(pathContent.content).replaceAll("<div class=\"cmd\">$1</div>");
        return pathContent;
    }
    
    
    public static PathContent spanCourier2Code(PathContent pathContent) {
        pathContent.content = patterns.get("span class=\"courier\"").matcher(pathContent.content).replaceAll("<code>$1</code>");
        return pathContent;
    }
    
    public static PathContent pre2PrettyPrint(PathContent pathContent, String lang) {
         pathContent.content = pathContent.content
                 .replaceAll("<pre>", "<pre class=\"prettyprint\"><code lang=\""+ lang + "\">")
                 .replaceAll("</pre>", "</code></pre>");
        return pathContent;
    }
    
    public static PathContent removeDivAside(PathContent pathContent) {
        pathContent.content = patterns.get("div class=\"aside\"").matcher(pathContent.content).replaceAll("");
        return pathContent;
    }
}
