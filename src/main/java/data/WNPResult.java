package data;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static java.nio.file.Files.readAllLines;

public class WNPResult {
    public final String result;

    public WNPResult() throws IOException {
        result = readAllLines(Paths.get("WNPResult.txt")).stream().collect(Collectors.joining("\n"));
    }
}
