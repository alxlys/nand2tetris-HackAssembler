package main;

import dictionaries.DictionariesFactory;
import parser.Parser;
import translator.Translator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {

    public static void main(String[] args) throws IOException {

        String asmFile = args[0];
        Path sourceFile = Paths.get(asmFile);

        Parser parser = new Parser(sourceFile);
        Translator translator = new Translator(
                parser,
                DictionariesFactory.initCTable(),
                DictionariesFactory.initDestJumpTable(),
                parser.getSymbolTable()
        );

        translator.translate();
    }

}
