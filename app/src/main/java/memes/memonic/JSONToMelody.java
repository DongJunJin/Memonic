package memes.memonic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tarunkhanna on 24/09/17.
 */

public class JSONToMelody extends JSONArray{
    public static ArrayList<String> arrEmotions = new ArrayList<>();
    public static ArrayList<Integer> arrMelodies = new ArrayList<>();
    public static Map<String,Integer> emotionToMelodyHashMap = new HashMap<>();

    String findMaxEmotion(JSONObject json) {
        json = json.optJSONObject("scores");
        Iterator<String> jsonKeys = json.keys();

        String maxEmotion = "";
        Double maxVal = Double.MIN_VALUE;
        while (jsonKeys.hasNext()) {
            String key = jsonKeys.next();
            try {
                Double temp = json.optDouble(key, Double.MIN_VALUE);
                if (temp > maxVal) {
                    maxEmotion = key;
                    maxVal = temp;
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return maxEmotion;
    }

    // wrapper to analyze each face
    ArrayList<String> findEachMaxEmotion(JSONArray jsonArr) {

        int size = jsonArr.length();
        for (int i = 0; i < size; i++) {
            try {
                this.arrEmotions.add(findMaxEmotion(jsonArr.getJSONObject(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.arrEmotions;
    }

    ArrayList<Integer> createMelodiesArray() {
        int size = this.arrEmotions.size();

        for (int i = 0; i < size; i++) {
            this.arrMelodies.add(emotionToMelodyHashMap.get(arrEmotions.get(i)));
        }

        return this.arrMelodies;
    }
}
