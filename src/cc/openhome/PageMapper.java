package cc.openhome;

import java.io.IOException;
import java.io.UncheckedIOException;
import static java.lang.System.out;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

/**
 *
 * @author Justin
 */
public class PageMapper {

    public static void main(String[] args) {
        List<String> htmlFiles = htmlFiles("c:\\workspace\\Java\\");
        out.println(htmlFiles);
        out.println(htmlFiles.stream()
                .map(PageMapper::fileContent)
                .findFirst().get());

    }

    private static List<String> htmlFiles(String dir) {
        try (Stream<Path> paths = withIO(() -> Files.list(Paths.get(dir)))) {
            return paths.map(Path::toString)
                    .filter(str -> str.endsWith(".html"))
                    .collect(toList());
        }
    }

    private static String fileContent(String dir) {
        return withIO(() -> Files.readAllLines(Paths.get(dir)).stream()
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
}
