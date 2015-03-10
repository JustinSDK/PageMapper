package cc.openhome.specific;

import cc.openhome.IO;
import cc.openhome.PageMapper;
import static cc.openhome.IO.htmlFiles;
import java.nio.file.Paths;
import java.util.List;

public class JavaMapper {
    public static void main(String[] args) {
        List<String> htmlFiles = htmlFiles(Paths.get("c:\\workspace\\NewJava\\"));
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
}
