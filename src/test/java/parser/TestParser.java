package parser;

import utils.FileUtil;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class TestParser {

    @Test
    public void testParser() throws URISyntaxException, IOException {

        Parser parser = new Parser(FileUtil.getFile("testParser.asm").toPath());

        while (true) {
            Parser.CodeLine codeLine = parser.nextLine();
            if (codeLine == null) {
                break;
            }
            System.out.println(codeLine);
        }

    }
}
