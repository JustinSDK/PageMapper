package cc.openhome;

import static cc.openhome.IO.htmlFiles;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Justin
 */
public class ProgrammerMapper {

    public static void main(String[] args) {
        List<String> htmlFiles = htmlFiles(Paths.get("c:\\workspace\\NewProgrammer\\"));
        htmlFiles.stream()
                .map(Paths::get)
                // 排除首頁，因為比較複雜，要手動修改
                .filter(path -> !path.getFileName().toString().equals("index.html"))
                .map(IO::pathContent)
                .map(PageMapper::map2Template)
                .map(PageMapper::spanCourier2Code)
                .forEach(IO::write);
    }
}
