package translator;

import parser.Parser;
import utils.BinaryUtil;
import utils.FileUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Translator {

    private static final String C_PREFIX = "prefix";

    private Parser parser;
    private Properties cTable;
    private Properties destJumpTable;
    private Map<String, Long> symbolsTable;
    private Path targetFile;

    public Translator(Parser parser, Properties cTable, Properties destJumpTable, Map<String, Long> symbolsTable) {
        this.parser = parser;
        this.cTable = cTable;
        this.destJumpTable = destJumpTable;
        this.symbolsTable = symbolsTable;

        Path sourceFile = parser.getSourceFile();
        String targetFileName = sourceFile.getFileName().toString().split("\\.")[0] + ".hack";
        this.targetFile = sourceFile.resolveSibling(targetFileName);
    }

    public void translate() throws IOException {
        List<String> translatedLines = new ArrayList<>();
        Parser.CodeLine codeLine;
        while ((codeLine = parser.nextLine()) != null) {
            String line;
            if (codeLine.isACmd()) {
                line = BinaryUtil.toBinaryString(codeLine.getAValue());
            } else {
                line = cTable.getProperty(C_PREFIX)
                        + cTable.getProperty(codeLine.getComp())
                        + destJumpTable.getProperty(codeLine.getDest())
                        + destJumpTable.getProperty(codeLine.getJump());
            }
            translatedLines.add(line + System.lineSeparator());
        }
        FileUtil.writeToFile(targetFile, translatedLines);
    }


}
