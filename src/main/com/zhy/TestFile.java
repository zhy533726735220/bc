package com.zhy;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * @author zhy 递归的找文件
 */
public class TestFile {
    protected void doRetrieveMatchingFiles(File dir, Set<File> result) throws IOException {
        File[] dirContents = dir.listFiles();
        if (dirContents == null) {
            return;
        }

        for (File content : dirContents) {
            if (content.isDirectory()) {
                if (!content.canRead()) {

                } else {
                    this.doRetrieveMatchingFiles(content, result);
                }
            } else {
                System.out.println(content.getPath());
                result.add(content);
            }
        }

    }
}
