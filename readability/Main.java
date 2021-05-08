package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static final Pattern VOWELS_EXCEPT_DOUBLES_PATTERN = Pattern.compile("[aeiouy](?![aeiouy])",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern LAST_E_VOWEL_PATTERN = Pattern.compile("e$", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {

        var fileName = readFileNameFromArgs(args);
        var pathToFile = Path.of(fileName);
        var content = readFileContent(pathToFile);

        var scanner = new Scanner(System.in);

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
        var syllables = calculateSyllables(words);
        var polysyllables = calculatePolysyllables(words);

        printTextStatistics(sentences, words, characters, syllables, polysyllables);
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
        String scoreToCalculate = scanner.nextLine();

        calculateAndPrintResult(scoreToCalculate, sentences.size(), words.size(),
                characters.size(), syllables, polysyllables);

    }

    private static void calculateAndPrintResult(
            String scoreToCalculate, int sentences, int words,
            int characters, int syllables, int polysyllables) {

        List<String> scoreMessages = new ArrayList<>();
        switch (scoreToCalculate) {
            case "ARI": {
                double ariScore = calculateARI(sentences, words, characters);
                scoreMessages.add(String.format("Automated Readability Index: %.2f (about %d-year-olds).%n",
                        ariScore, getUnderstoodAge(Math.round(ariScore))));
                break;
            }
            case "FK": {
                double fleshKincaidScore = calculateFleschKincaidScore(words, sentences, syllables);
                scoreMessages.add(String.format("Flesch–Kincaid readability tests: %.2f (about %d-year-olds).%n",
                        fleshKincaidScore, getUnderstoodAge(Math.round(fleshKincaidScore))));
                break;
            }
            case "SMOG": {
                double smog = calculateSMOG(sentences, polysyllables);
                scoreMessages.add(String.format("Simple Measure of Gobbledygook: %.2f (about %d-year-olds).%n",
                        smog, getUnderstoodAge(Math.round(smog))));
                break;
            }
            case "CL": {
                double colemanIndex = calculateColemanLiauIndex(words, sentences, characters);
                scoreMessages.add(String.format("Coleman–Liau index: %.2f (about %d-year-olds).%n",
                        colemanIndex, getUnderstoodAge(Math.round(colemanIndex))));
                break;
            }
            default:
                double ariScore = calculateARI(sentences, words, characters);
                double fleshKincaidScore = calculateFleschKincaidScore(words, sentences, syllables);
                double smog = calculateSMOG(sentences, polysyllables);
                double colemanIndex = calculateColemanLiauIndex(words, sentences, characters);
                scoreMessages.add(String.format("Automated Readability Index: %.2f (about %d-year-olds).%n",
                        ariScore, getUnderstoodAge(Math.round(ariScore))));
                scoreMessages.add(String.format("Flesch–Kincaid readability tests: %.2f (about %d-year-olds).%n",
                        fleshKincaidScore, getUnderstoodAge(Math.round(fleshKincaidScore))));
                scoreMessages.add(String.format("Simple Measure of Gobbledygook: %.2f (about %d-year-olds).%n",
                        smog, getUnderstoodAge(Math.round(smog))));
                scoreMessages.add(String.format("Coleman–Liau index: %.2f (about %d-year-olds).%n",
                        colemanIndex, getUnderstoodAge(Math.round(colemanIndex))));
        }
        scoreMessages.forEach(System.out::print);
    }

    private static void printTextStatistics(List<String> sentences, List<String> words, List<String> characters, int syllables, int polysyllables) {
        System.out.println("Words: " + words.size());
        System.out.println("Sentences: " + sentences.size());
        System.out.println("Characters: " + characters.size());
        System.out.println("Syllables: " + syllables);
        System.out.println("Polysyllables: " + polysyllables);
    }

    private static double calculateColemanLiauIndex(int words, int sentences, int characters) {
        double l = (double) characters / words * 100;
        double s = (double) sentences / words * 100;

        return 0.0588 * l - 0.296 * s - 15.8;
    }

    private static int calculatePolysyllables(List<String> words) {
        var polysyllables = 0;
        for (var word : words) {
            int syllables = getSyllablesCount(word);
            if (syllables > 2) {
                polysyllables++;
            }
        }

        return polysyllables;
    }

    private static double calculateARI(int sentences, int words, int characters) {
        return 4.71 * ((double) characters / words)
                + 0.5 * ((double) words / sentences) - 21.43;
    }

    private static double calculateFleschKincaidScore(int words, int sentences, int syllables) {
        return 0.39 * ((double) words / sentences) + 11.8 * ((double) syllables / words) - 15.59;
    }

    private static int calculateSyllables(List<String> words) {
        var syllablesCount = 0;
        for (var word : words) {
            syllablesCount += getSyllablesCount(word);
        }

        return syllablesCount;
    }

    private static double calculateSMOG(int sentences, int polysyllables) {
        return 1.043 * Math.sqrt(polysyllables * ((double) 30 / sentences)) + 3.1291;
    }

    private static int getSyllablesCount(String word) {
        var vowelsCount = 0;
        var vowelsMatcher = VOWELS_EXCEPT_DOUBLES_PATTERN.matcher(word);
        vowelsCount += vowelsMatcher.results().count();
        if (LAST_E_VOWEL_PATTERN.matcher(word).find()) {
            vowelsCount--;
        }

        if (vowelsCount == 0) {
            vowelsCount = 1;
        }
        return vowelsCount;
    }

    private static int getUnderstoodAge(long score) {
        int age = 0;
        switch ((int) score) {
            case (1): {
                age = 6;
                break;
            }
            case (2): {
                age = 7;
                break;
            }
            case (3): {
                age = 9;
                break;
            }
            case (4): {
                age = 10;
                break;
            }
            case (5): {
                age = 11;
                break;
            }
            case (6): {
                age = 12;
                break;
            }
            case (7): {
                age = 13;
                break;
            }
            case (8): {
                age = 14;
                break;
            }
            case (9): {
                age = 15;
                break;
            }
            case (10): {
                age = 16;
                break;
            }
            case (11): {
                age = 17;
                break;
            }
            case (12): {
                age = 18;
                break;
            }
            case (13): {
                age = 24;
                break;
            }
        }

        return age;
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
