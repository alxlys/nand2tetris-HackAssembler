package parser;

import dictionaries.DictionariesFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Parser {

    private static final String COMMENT = "//";

    public static class CodeLine {

        private boolean isACmd;
        private Long aValue;
        private String dest;
        private String comp;
        private String jump;
        private long lineNumber;

        private CodeLine(boolean isACmd, Long aValue, long lineNumber) {
            this.isACmd = isACmd;
            this.aValue = aValue;
            this.lineNumber = lineNumber;
        }

        public CodeLine(boolean isACmd, String dest, String comp, String jump, long lineNumber) {
            this.isACmd = isACmd;
            this.dest = isBlank(dest) ? "null" : dest;
            this.comp = comp;
            this.jump = isBlank(jump) ? "null" : jump;
            this.lineNumber = lineNumber;
        }

        public boolean isACmd() {
            return isACmd;
        }

        public String getDest() {
            return dest;
        }

        public String getComp() {
            return comp;
        }

        public String getJump() {
            return jump;
        }

        public long getLineNumber() {
            return lineNumber;
        }

        public Long getAValue() {
            return aValue;
        }

        @Override
        public String toString() {
            return "CodeLine{" +
                    "isACmd=" + isACmd +
                    ", aValue='" + aValue + '\'' +
                    ", dest='" + dest + '\'' +
                    ", comp='" + comp + '\'' +
                    ", jump='" + jump + '\'' +
                    ", lineNumber=" + lineNumber +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CodeLine codeLine = (CodeLine) o;
            return isACmd == codeLine.isACmd &&
                    lineNumber == codeLine.lineNumber &&
                    Objects.equals(aValue, codeLine.aValue) &&
                    Objects.equals(dest, codeLine.dest) &&
                    Objects.equals(comp, codeLine.comp) &&
                    Objects.equals(jump, codeLine.jump);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isACmd, aValue, dest, comp, jump, lineNumber);
        }
    }

    private Path sourceFile;
    private List<String> allLines;
    private Iterator<String> iterator;
    private long lineNumber = 0L;
    private Map<String, Long> symbolTable;
    private long varNextRegister = 16L;
    private Pattern labelPattern = Pattern.compile("\\(([^\\)]*)\\)");
    private Pattern aPattern = Pattern.compile("@(.*)");
    private Pattern cPatternDest = Pattern.compile("([^=;]*)=([^=;]*)");
    private Pattern cPatternJump = Pattern.compile("([^=;]*);([^=;]*)");

    public Parser(Path sourceFile) throws IOException {
        this.sourceFile = sourceFile;
        this.allLines = Files.readAllLines(sourceFile);
        this.iterator = allLines.iterator();
        this.symbolTable = DictionariesFactory.initSymbolsTable();
        fillLabels();
    }

    public Path getSourceFile() {
        return sourceFile;
    }

    public Map<String, Long> getSymbolTable() {
        return Collections.unmodifiableMap(symbolTable);
    }

    public CodeLine nextLine() {
        if (iterator.hasNext()) {
            String codeLine = iterator.next();
            if (isEmptyLine(codeLine)) {
                return nextLine();
            }
            codeLine = cleanLine(codeLine);
            if (parseLabel(codeLine) != null) {
                return nextLine();
            }
            return parse(codeLine);
        }
        return null;

    }

    private void fillLabels() {
        long lineNum = 0L;
        for (String line : allLines) {
            if (isEmptyLine(line)) {
                continue;
            }
            line = cleanLine(line);
            String label;
            if ((label = parseLabel(line)) != null) {
                symbolTable.put(label, lineNum);
            } else {
                lineNum++;
            }
        }
    }

    private String parseLabel(String codeLine) {
        Matcher labelMatcher = labelPattern.matcher(codeLine);
        if (labelMatcher.find()) {
            return labelMatcher.group(1);
        }
        return null;
    }

    private boolean isEmptyLine(String codeLine) {
        codeLine = codeLine.trim();
        return isBlank(codeLine) || codeLine.startsWith(COMMENT);
    }

    private String cleanLine(String codeLine) {
        codeLine = codeLine.replaceAll("\\s+", "");
        int offset = codeLine.indexOf(COMMENT);
        if (offset != -1) {
            codeLine = codeLine.substring(0, offset);
        }
        return codeLine;
    }

    private CodeLine parse(String codeLine) {
        Matcher aMatcher = aPattern.matcher(codeLine);
        if (aMatcher.find()) {
            String address = aMatcher.group(1);
            Long addrValue;
            if (StringUtils.isNumeric(address)) {
                addrValue = Long.valueOf(address);
            } else if (symbolTable.containsKey(address)) {
                addrValue = symbolTable.get(address);
            } else {
                addrValue = varNextRegister++;
                symbolTable.put(address, addrValue);
            }
            return new CodeLine(
                    true,
                    addrValue,
                    lineNumber++);
        }

        String dest = null;
        String comp = null;
        String jump = null;

        Matcher cMatcherDest = cPatternDest.matcher(codeLine);
        if (cMatcherDest.find()) {
            dest = cMatcherDest.group(1);
            comp = cMatcherDest.group(2);
        }
        Matcher cMatcherJump = cPatternJump.matcher(codeLine);
        if (cMatcherJump.find()) {
            comp = cMatcherJump.group(1);
            jump = cMatcherJump.group(2);
        }
        return new CodeLine(false, dest, comp, jump, lineNumber++);
    }
}
