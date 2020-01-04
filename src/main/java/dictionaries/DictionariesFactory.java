package dictionaries;

import utils.FileUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class DictionariesFactory {

    public static Properties initCTable() throws IOException {
        return FileUtil.loadProperties("comp.properties");
    }

    public static Properties initDestJumpTable() throws IOException {
        return FileUtil.loadProperties("dest.properties", "jump.properties");
    }

    public static Map<String, Long> initSymbolsTable() throws IOException {
        return FileUtil.loadProperties("symbols.properties").entrySet().stream().
                collect(Collectors.toMap(e -> (String) e.getKey(),
                        e -> Long.valueOf((String) e.getValue())));
    }

}
