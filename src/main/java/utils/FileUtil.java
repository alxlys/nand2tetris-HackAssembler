package utils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileUtil {

    public static Properties loadProperties(String... fileName) throws IOException {
        Properties props = new Properties();
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        for (String file : fileName) {
            props.load(classLoader.getResourceAsStream(file));
        }
        return props;
    }

    public static File getFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        return new File(classLoader.getResource(fileName).toURI());
    }

    public static void writeToFile(Path targetFile, List<String> lines) throws IOException {
        try (OutputStream out = Files.newOutputStream(targetFile)) {
            for (String line : lines) {
                IOUtils.write(line, out, UTF_8);
            }
        }
    }
}
