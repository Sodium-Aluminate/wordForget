package alpt;

import java.util.*;

public class WordManager {
    private HashMap<String, ForgetInfo> wordMap;

    private ArrayList<String> words;

    public static class Builder{
        private static HashMap<String, ForgetInfo> fromSave(String save){
            HashMap<String, ForgetInfo> wordMap = new HashMap<>();
            String[] s1 = save.split("\n");
            for (String s2:s1) {
                String[] s3 = s2.split("=");
                wordMap.put(s3[0], new ForgetInfo(s3[1]));
            }
            return wordMap;
        }

        public static WordManager restore(String save, Collection<String> words){

            HashMap<String, ForgetInfo> wordMap = new HashMap<>();
            HashMap<String, ForgetInfo> fromMap = fromSave(save);
            for (String word:words) {
                wordMap.put(word, new ForgetInfo());
            }

            for(Map.Entry<String, ForgetInfo> entry:fromMap.entrySet()){
                if(wordMap.containsKey(entry.getKey()))
                    wordMap.put(entry.getKey(),entry.getValue());
            }
            return new WordManager(wordMap);
        }

        public static WordManager build(Collection<String> words){
            HashMap<String, ForgetInfo> wordMap = new HashMap<>();
            for (String word:words) {
                wordMap.put(word, new ForgetInfo());
            }
            return new WordManager(wordMap);
        }
    }

    private WordManager(HashMap<String, ForgetInfo> wordMap){
        this.wordMap = wordMap;
        words = new ArrayList<>(wordMap.keySet());
        sort();
    }

    public void sort(){
        words.sort((s1, s2)->{
            long curTime = System.currentTimeMillis();
            WeightCalculator weightCalculator = (pass, total, time) -> {
                double delta = (curTime-time);
                return (double) 25*(pass+2)/(total+8)/Math.log(delta+100);
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

    public ForgetInfo get(String word){
        return wordMap.get(word);
    }

    public String save(String originalString){
        HashMap<String, ForgetInfo> saveMap;
        if(originalString==null){
            saveMap = wordMap;
        }else {
            saveMap = Builder.fromSave(originalString);
            for (Map.Entry<String,ForgetInfo> entry:wordMap.entrySet()) {
                saveMap.put(entry.getKey(),entry.getValue());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,ForgetInfo> entry:saveMap.entrySet()){
            String key = entry.getKey();
            ForgetInfo value = entry.getValue();
            if(value.isNew()) continue;
            sb.append(key).append('=').append(value).append('\n');
        }

        return sb.toString();

    }

    public int size(){
        return wordMap.size();
    }

    public int oldCount() {
        int i = 0;
        for (ForgetInfo w:wordMap.values()) {
            if(!w.isNew()) i++;
        }
        return i;
    }

}
