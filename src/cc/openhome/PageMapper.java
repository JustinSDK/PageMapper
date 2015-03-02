package cc.openhome;

import java.io.IOException;
import java.io.UncheckedIOException;
import static java.lang.System.out;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

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
    }    
            
    public static void main(String[] args) {
        List<String> htmlFiles = htmlFiles(Paths.get("c:\\workspace\\NewJava\\"));
        htmlFiles.stream()
                .map(Paths::get)
                // 排除首頁，因為比較複雜，要手動修改
                .filter(path -> !path.getFileName().toString().equals("index.html"))
                .map(PageMapper::pathContent)
                // 處理 div class="article" 與 title
                .map(PageMapper::map)
                .forEach(pathContent -> {
                    //withIO(() -> Files.write(pathContent.path, pathContent.content.getBytes("UTF-8"), TRUNCATE_EXISTING, CREATE));
                });
        
    }

    private static List<String> htmlFiles(Path path) {
        try (Stream<Path> paths = withIO(() -> Files.list(path))) {
            return paths.map(Path::toString)
                     .filter(str -> str.endsWith(".html"))
                     .collect(toList());
        }
    }

    private static PathContent pathContent(Path path) {
        String content = withIO(() -> Files.readAllLines(path).stream()
                .reduce((acc, line) -> acc + line + System.getProperty("line.separator"))
                .get());
        return new PathContent(path, content);
    }

    private static <R> R withIO(UncheckedIO<R> io) {
        try {
            return io.run();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
    
    private static String tagContent(String content, String tag) {
        Matcher matcher =  patterns.get(tag).matcher(content);
        matcher.find();
        return matcher.group(1);
    }
     
    private static PathContent map(PathContent pathContent) {
         pathContent.content = 
             template.content
                   .replace("#content#", tagContent(pathContent.content, "div class=\"article\""))
                   .replaceAll("#title#", tagContent(pathContent.content, "title"));
         return pathContent;
    }
}
