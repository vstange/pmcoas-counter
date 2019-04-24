package de.vstange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final Map<String, Integer> MAP_COUNTER = new ConcurrentHashMap<>();

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private static final Pattern PATTERN = Pattern.compile("article-type=\"(.*)\"");

    public static void main(String[] args) throws IOException {

        Path directory = Paths.get(args[0]);
        assert Files.isDirectory(directory);

        Files.walk(directory).parallel().forEach(path -> {
            if (path.endsWith("tar.gz"))
                return;
            if (Files.isDirectory(path))
                return;

            //System.out.println(COUNTER.incrementAndGet() + " " + path.toString());
            String type;
            try {
                type = getArticleType(Files.readString(path));
            } catch (IOException e) {
                System.err.println(path.toString());
                e.printStackTrace();
                type = "error";
            } catch (OutOfMemoryError e) {
                System.err.println(path.toString());
                e.printStackTrace();
                type = "outofmemory";
            }

            MAP_COUNTER.compute(type, (k, v) -> v == null ? 1 : v + 1);
        });

        MAP_COUNTER.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .forEach(entry -> System.out.println(String.format("%s: %d", entry.getKey(), entry.getValue())));
    }

    static String getArticleType(String content) {
        int prefixIndex = content.indexOf("<article");
        if (prefixIndex > -1) {
            int endIndex = content.indexOf(">", prefixIndex + 10);
            if (endIndex > -1) {
                String articleString = content.substring(prefixIndex, endIndex);
                Matcher matcher = PATTERN.matcher(articleString);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }

        return null;
    }
}
