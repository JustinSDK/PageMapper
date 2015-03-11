/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cc.openhome.specific;

import cc.openhome.IO;
import cc.openhome.PageMapper;
import static cc.openhome.IO.htmlFiles;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Justin
 */
public class JUnitMapper {
        public static void main(String[] args) {
        List<String> htmlFiles = htmlFiles(Paths.get("c:\\workspace\\JUnit\\"));
        htmlFiles.stream()
                .map(Paths::get)
                // 排除首頁，因為比較複雜，要手動修改
                .filter(path -> !path.getFileName().toString().equals("index.html"))
                .map(path -> IO.pathContent(path, Charset.forName("Big5")))
                .map(pathContent -> PageMapper.titleTdArticle2Template(pathContent, "tdStudyGossip"))
                .map(PageMapper::img2RWD)
                .map(pathContent -> PageMapper.pre2PrettyPrint(pathContent, "java"))
                .forEach(IO::write);
    }
}
