package parser;

import dictionaries.DictionariesFactory;
import org.junit.Test;
import translator.Translator;
import utils.FileUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

public class TestTranslator {

    @Test
    public void testTranslator() throws URISyntaxException, IOException {

        Parser parser = new Parser(FileUtil.getFile("testParser.asm").toPath());
        Properties cTable = DictionariesFactory.initCTable();
        Properties destJumpTable = DictionariesFactory.initDestJumpTable();
        Map<String, Long> symbolsTable = DictionariesFactory.initSymbolsTable();

        Translator translator = new Translator(parser, cTable, destJumpTable, symbolsTable);
        translator.translate();

    }

}
