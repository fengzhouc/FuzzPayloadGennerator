package com.alumm0x.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SourceLoader {

    public static List<String> loadSources(String filepath){
        List<String> payloads = new ArrayList<>();
        InputStream inStream = SourceLoader.class.getResourceAsStream(filepath);
        assert inStream != null;
        try(Scanner scanner = new Scanner(inStream)){
            while (scanner.hasNextLine()){
                payloads.add(scanner.nextLine());
            }
        }
        return payloads;
    }
}
