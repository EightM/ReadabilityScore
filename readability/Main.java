package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        var fileName = readFileNameFromArgs(args);
        var pathToFile = Path.of(fileName);
        var content = readFileContent(pathToFile);

        if (content.isEmpty()) {
            return;
        }

        var sentences = Arrays.stream(content.split("[.?!]"))
                .map(String::trim)
                .collect(Collectors.toList());
        var words = sentences.stream()
                .flatMap(sentence -> Arrays.stream(sentence.split("\\s+")))
                .collect(Collectors.toList());
        var characters = Arrays.stream(content.split("\\s+"))
                .flatMap(word -> Arrays.stream(word.split("")))
                .collect(Collectors.toList());

        double score = 4.71 * ((double) characters.size() / words.size()) + 0.5 * ((double) words.size() / sentences.size()) - 21.43;

        System.out.println("Words: " + words.size());
        System.out.println("Sentences: " + sentences.size());
        System.out.println("Characters: " + characters.size());
        System.out.printf("The score is: %.2f%n", score);
        printUnderstoodAge((int) Math.ceil(score));
    }

    private static void printUnderstoodAge(int score) {
        var age = "";
        switch (score) {
            case (1): {
                age = "5-6";
                break;
            }
            case  (2): {
                age = "6-7";
                break;
            }
            case  (3): {
                age = "7-9";
                break;
            }
            case  (4): {
                age = "9-10";
                break;
            }
            case  (5): {
                age = "10-11";
                break;
            }
            case  (6): {
                age = "11-12";
                break;
            }
            case  (7): {
                age = "12-13";
                break;
            }
            case  (8): {
                age = "13-14";
                break;
            }
            case  (9): {
                age = "14-15";
                break;
            }
            case  (10): {
                age = "15-16";
                break;
            }
            case  (11): {
                age = "16-17";
                break;
            }
            case  (12): {
                age = "17-18";
                break;
            }
            case  (13): {
                age = "18-24";
                break;
            }
            case  (14): {
                age = "24+";
                break;
            }
        }

        System.out.println("This text should be understood by " + age + "-year-olds.");
    }

    private static String readFileContent(Path pathToFile) {
        var content = "";
        try {
            content = Files.readString(pathToFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private static String readFileNameFromArgs(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException();
        }

        return args[0];
    }
}
