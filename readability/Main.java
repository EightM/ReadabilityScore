package readability;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var text = scanner.nextLine();

        var sentences = Arrays.stream(text.split("[.?!]")).collect(Collectors.toList());

        var sentencesSum = sentences.stream()
                .map(sentence -> sentence.split("\\s+"))
                .mapToInt(sentence -> sentence.length).sum();

        if (sentencesSum / sentences.size() > 10) {
            System.out.println("HARD");
        } else {
            System.out.println("EASY");
        }
    }
}
