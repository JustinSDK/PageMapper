package cc.openhome;

import java.io.IOException;
import java.io.UncheckedIOException;
import static java.lang.System.out;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static String template = fileContent(Paths.get("c:\\workspace\\template.html"));
    
    private static Map<String, Pattern> patterns = new HashMap<>();
    static {
        patterns.put("title", Pattern.compile("<title>(.*)</title>"));
        patterns.put("body", Pattern.compile("<body>((.*\\s*)*)</body>"));
    }    
            
    public static void main(String[] args) {
        List<String> htmlFiles = htmlFiles(Paths.get("c:\\workspace\\Java\\"));
        String content = htmlFiles.stream()
                .map(Paths::get)
                .map(PageMapper::fileContent)
                .findFirst().get();
        
        out.println(tagContent(content, "title"));
        out.println(tagContent(content, "body"));
        

    }

    private static List<String> htmlFiles(Path path) {
        try (Stream<Path> paths = withIO(() -> Files.list(path))) {
            return paths.map(Path::toString)
                     .filter(str -> str.endsWith(".html"))
                     .collect(toList());
        }
    }

    private static String fileContent(Path path) {
        return withIO(() -> Files.readAllLines(path).stream()
                .reduce((acc, line) -> acc + line + System.getProperty("line.separator"))
                .get());
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
   
}
