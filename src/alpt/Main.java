package alpt;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class Main {
    static VocabularyManager meaning = null;
    static WordManager words = null;
    static Scanner scanner = new Scanner(System.in);
    static String path;
    static String savePath;
    static boolean init = false;
    static String stupidClearWay = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
    static boolean startRightNow = false;

    public static void main(String[] args) throws IOException {
        for (String s : args) {
            if (s.toLowerCase().matches("-{0,2}stupid[-_]?clean")) {
                clearWay = () -> print(stupidClearWay);
                continue;
            }
            if (s.startsWith("path=")) {
                path = s.substring(5);
                continue;
            }
            if (s.startsWith("savePath=")) {
                savePath = s.substring(9);
                continue;
            }
            if (s.equalsIgnoreCase("fastStart")){
                startRightNow = true;
            }
        }

        init();

        String[] input;
        while (true) {
            if (!init)
                println("not init yet, use \"import\"\n\timport path_of_file [path_of_save]\tdefault save file will be create as \"original_name\".save ");
            else
                println("input \"start\" to start.");
            if(startRightNow){
                println("auto started.");
                input = new String[]{"start"};
                startRightNow = false;
            }else {
                input = scanner.nextLine().split(" ");
            }
            switch (input[0].toLowerCase()) {
                case "import":
                    path = input[1];
                    if (input.length > 2)
                        savePath = input[2];
                    init();
                    break;
                case "start":
                case "s":
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
                    int count;
                    if( input.length > 1 ){
                        if(input[1].equalsIgnoreCase("all")){
                            count = words.size();
                        }else {
                            count = Integer.parseInt(input[1]);
                        }
                    } else count = 10;
                    words.sort();
                    String[] ws = words.get(count);
                    for (String w : ws) {
                        print(words.get(w).score() + "\t" + calP(words.get(w)));
                        print("\t");
                        print(w);
                        print(": ");
                        print(meaning.getMeaning(w));
                        println("");
                    }
                    break;
                default:
                    println("?");
            }
        }
    }

    private static void init() throws IOException {
        if (path == null) return;
        if (savePath == null) savePath = path + ".save";
        meaning = new VocabularyManager(new File(path));
        if (new File(savePath).isFile()) {
            words = WordManager.Builder.restore(new String(Files.readAllBytes(pathOf(savePath))), meaning.getWords());
            println("import finished, " + words.oldCount() + " saves imported, total " + meaning.size() + " words");
        } else {
            words = WordManager.Builder.build(meaning.getWords());
            println("import finished, total " + meaning.size() + " words");
        }
        init = true;
    }


    public static void main() throws IOException {
        println("1 for remember, 0 for forgot, space for show meanings, k for skip, u for undo the last skip, q for exit, w for save");
        ForgetInfo forgetInfo;
        ForgetInfo lastSkip = null;
        String lastSkipWord = null;
        boolean showMeaning;

        while (true) {
            for (String word : words.get(6)) {
                forgetInfo = words.get(word);
                clear();
                int spaces = 20 - word.length();
                print("\n"+word + repeat(" ", spaces) + forgetInfo.score() + "\t" + calP(forgetInfo) + "\t");
                showMeaning = false;
                ask: while (true) {
                    switch (scanner.nextLine().toLowerCase().replaceAll("[^0-9a-z]", "")) {
                        case "1":
                            forgetInfo.passed();
                            break ask;
                        case "0":
                            forgetInfo.failed();
                            break ask;
                        case "":
                            if(showMeaning)break;
                            println(meaning.getMeaning(word) + "\t\thttps://www.google.com/search?q=" + word + "+pronunciation");
                            showMeaning = true;
                            break;
                        case "k":
                            forgetInfo.skip();
                            lastSkip = forgetInfo;
                            lastSkipWord = word;
                            break ask;
                        case "u":
                            if(lastSkip==null) {
                                println("nothing to undo");
                                break;
                            }
                            if (lastSkip.uSkip()) {
                                print("re");
                            } else {
                                print("un");
                            }
                            println("-skipped \""+lastSkipWord+"\"");
                            break;
                        case "q":
                            save();
                            return;
                        case "w":
                            save();
                            print(word + "\t" + forgetInfo.score() + "\t" + calP(forgetInfo)+"\t");
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

    public static Path pathOf(String first, String... more) {
        return FileSystems.getDefault().getPath(first, more);
    }

    private static void save() throws IOException {
        print("\nsaving...");
        File save = new File(savePath);
        words.sort();
        String oldString = save.exists()?new String(Files.readAllBytes(pathOf(savePath))):null;
        String newString = words.save(oldString);
        if (!save.exists()) {
            save.createNewFile();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(savePath))) {
            bw.write(newString);
        }
        clear();
        println("\tsaved\n");
    }

    private static final String[] spaces = {"", " ", "  ", "   ", "    ", "     ", "      "};
    private static String calP(ForgetInfo forgetInfo) {
        long curTime = System.currentTimeMillis();
        if(forgetInfo.isSkipped())return "SKIPPED";
        String toReturn = String.valueOf(forgetInfo.weight((pass, total, time) -> {
            double delta = (curTime - time);
            return (double) 35*(pass+2)/(total+8)/Math.log(delta+100);
        }));
        return toReturn.length()>7?toReturn.substring(0,7):(toReturn+spaces[7-toReturn.length()]);
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


    private static String repeat(String s, int time){
        StringBuilder stringBuilder = new StringBuilder();
        if(time <1 )time = 1;
        for (int i = 0; i < time; i++) {
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }
}
