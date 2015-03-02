package cc.openhome;

import java.io.IOException;
import static java.lang.System.out;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.util.Collections.emptyList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

/**
 *
 * @author Justin
 */
public class PageMapper {
    public static void main(String[] args) throws IOException {
        List<String> htmlFiles = htmlFiles("c:\\workspace\\Java\\");
        out.println(htmlFiles);
    }

    private static List<String> htmlFiles(String dir) throws IOException {
        List<String> htmlFiles = emptyList();
        try(Stream<Path> paths = Files.list(Paths.get(dir))) {
            htmlFiles = paths.map(Path::toString)
                           .filter(str -> str.endsWith(".html"))
                           .collect(toList());            
        }
        return htmlFiles;
    }
}
