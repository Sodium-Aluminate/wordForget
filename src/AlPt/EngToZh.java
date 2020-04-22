package AlPt;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class EngToZh {
    static MeaningManager meaning = null;
    static WordManager words = null;
    static Scanner scanner = new Scanner(System.in);
    static String path;
    static String savePath;
    static boolean init = false;

    public static void main(String[] args) throws IOException {
        for (String s : args) {
            if (s.toLowerCase().matches("-{0,2}stupid[-_]?clean")) {
                clearWay = () -> print("\n".repeat(100));
            }
            if (s.startsWith("path="))
                path = s.substring(5);
            if (s.startsWith("savePath="))
                savePath = s.substring(9);
        }

        init();

        String[] input;
        while (true) {
            if (!init)
                println("not init yet, use \"import\"\n\timport path_of_file [path_of_save]\tdefault save file will be create as \"original_name\".save ");
            else
                println("input \"start\" to start.");
            input = scanner.nextLine().split(" ");
            switch (input[0].toLowerCase()) {
                case "import":
                    path = input[1];
                    if (input.length > 2)
                        savePath = input[2];
                    init();
                    break;
                case "start":
                    if (!init) {
                        println("import anything first");
                        continue;
                    }
                    main();
                    break;
                case "q":
                    return;
                case "p":
                    words.sort();
                    int count = input.length > 1 ? Integer.parseInt(input[1]) : 10;
                    String[] ws = words.get(count);
                    for (String w : ws) {
                        print(words.get(w).score() + "\t" + calP(words.get(w)));
                        print("\t");
                        print(w);
                        print(": ");
                        print(meaning.getMeaning(w));
                        println("");
                    }
                default:
                    println("?");
            }
        }
    }

    private static void init() throws IOException {
        if (path == null) return;
        if (savePath == null) savePath = path + ".save";
        meaning = new MeaningManager(new File(path));
        if (new File(savePath).isFile())
            words = WordManager.Builder.restore(new String(Files.readAllBytes(Path.of(savePath))), meaning.getWords());
        else
            words = WordManager.Builder.build(meaning.getWords());
        println("import finished, " + words.size() + " saves imported, total "+meaning.size()+" words");
        init = true;
    }


    public static void main() throws IOException {
        println("1 for remember, 0 for forgot, space for show meanings, k for skip, u for undo the last skip, q for exit, w for save");
        WordInfo wordInfo;
        WordInfo lastSkip = null;
        boolean showMeaning;

        while (true) {
            for (String word : words.get(10)) {
                wordInfo = words.get(word);
                clear();
                print("\n"+word + "\t" + wordInfo.score() + "\t" + calP(wordInfo) + "\t");
                showMeaning = false;
                ask: while (true) {
                    switch (scanner.nextLine().toLowerCase().replaceAll("[^0-9a-z]", "")) {
                        case "1":
                            wordInfo.passed();
                            break ask;
                        case "0":
                            wordInfo.failed();
                            break ask;
                        case "":
                            if(showMeaning)break;
                            println(meaning.getMeaning(word) + "\t\thttps://www.google.com/search?q=" + word + "+pronunciation");
                            showMeaning = true;
                            break;
                        case "k":
                            wordInfo.skip();
                            lastSkip = wordInfo;
                            break ask;
                        case "u":
                            if(lastSkip==null) {
                                println("nothing to undo");
                                break;
                            }
                            if (lastSkip.uSkip()) {

                                println("re-skipped");
                            } else {
                                println("un-skipped");
                            }
                            break;
                        case "q":
                            save();
                            return;
                        case "w":
                            save();
                            print(word + "\t" + wordInfo.score() + "\t" + calP(wordInfo)+"\t");
                            break;
                        default:
                            println("?");
                    }
                }
                if(!showMeaning)println(meaning.getMeaning(word));
            }
            save();
        }
    }

    private static void save() throws IOException {
        print("\nsaving...");
        File save = new File(savePath);
        words.sort();
        if (!save.exists()) {
            save.createNewFile();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(savePath))) {
            bw.write(words.backup());
        }
        clear();
        println("\tsaved\n");
    }

    private static double calP(WordInfo wordInfo) {
        long curTime = System.currentTimeMillis();
        return wordInfo.weight((pass, total, time) -> {
            double delta = (curTime - time);
            return (double) 25 * pass / (total + 4) / Math.log(delta + 10 * 1000);
        });
    }

    public static void println(Object o) {
        System.out.println(o);
    }

    public static void print(Object o) {
        System.out.print(o);
    }

    public static void clear() {
        clearWay.run();
    }

    private static Runnable clearWay = () -> {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException ignore) {
        }
    };
}
