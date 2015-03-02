package cc.openhome;

import java.nio.file.Path;

public class PathContent {
    Path path;
    String content;

    public PathContent(Path path, String content) {
        this.path = path;
        this.content = content;
    }
    
}
