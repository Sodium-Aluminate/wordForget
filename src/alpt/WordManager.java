package alpt;

import java.util.*;

public class WordManager {
    private HashMap<String, WordInfo> wordMap;

    private ArrayList<String> words;

    public static class Builder{
        public static WordManager restore(String from, Collection<String> words){

            HashMap<String, WordInfo> wordMap = new HashMap<>();
            for (String word:words) {
                wordMap.put(word, new WordInfo());
            }
            String[] s1 = from.split("\n");
            for (String s2:s1) {
                String[] s3 = s2.split("=");
                wordMap.put(s3[0], new WordInfo(s3[1]));
            }
            return new WordManager(wordMap);
        }

        public static WordManager build(Collection<String> words){
            HashMap<String, WordInfo> wordMap = new HashMap<>();
            for (String word:words) {
                wordMap.put(word, new WordInfo());
            }
            return new WordManager(wordMap);
        }
    }

    private WordManager(HashMap<String, WordInfo> wordMap){
        this.wordMap = wordMap;
        words = new ArrayList<>(wordMap.keySet());
        sort();
    }

    public void sort(){
        words.sort((s1, s2)->{
            long curTime = System.currentTimeMillis();
            WeightCalculator weightCalculator = (pass, total, time) -> {
                double delta = (curTime-time);
                return (double) 25*pass/(total+4)/Math.log(delta+100);
            };
            if(s1.equals(s2))return 0;
            double d = (wordMap.get(s1).weight(weightCalculator) - wordMap.get(s2).weight(weightCalculator));
            if(d>0)return (int) (d+1);
            if(d<0)return (int) (d-1);
            return 0;
        });
    }

    public String[] get(int count){
        sort();
        if(count<1)count=1;
        else if(count>words.size())count = words.size();
        String[] s = new String[count];
        Iterator<String> iterator = words.iterator();
        for (int i = 0; i < count; i++) {
            s[i] = iterator.next();
        }
        return s;
    }

    public WordInfo get(String word){
        return wordMap.get(word);
    }

    public String backup(){

        Iterator<Map.Entry<String, WordInfo>> i = wordMap.entrySet().iterator();

            StringBuilder sb = new StringBuilder();
            while(true) {
                if (!i.hasNext()) {
                    return sb.toString();
                }
                Map.Entry<String, WordInfo> e = i.next();
                String key = e.getKey();
                WordInfo value = e.getValue();
                if(value.isNew()) continue;
                sb.append(key);
                sb.append('=');
                sb.append(value);
                sb.append('\n');
            }

    }

    public int size(){
        return wordMap.size();
    }

    public int oldCount() {
        int i = 0;
        for (WordInfo w:wordMap.values()) {
            if(!w.isNew()) i++;
        }
        return i;
    }

}
