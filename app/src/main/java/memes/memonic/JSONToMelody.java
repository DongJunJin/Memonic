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

public class JSONToMelody extends JSONArray {
    public static ArrayList<String> arrEmotions = new ArrayList<>();
    public static ArrayList<Integer> arrMelodies = new ArrayList<>();
    public static Map<String,Integer> emotionToMelodyHashMap = new HashMap<>();

    static String findMaxEmotionFromEmotionsArrayObject(JSONObject emotionsJson) {

        String maxEmotion = "";
        Double maxEmotionDbl = Double.MIN_VALUE;

        try {
            Iterator<String> emotions = emotionsJson.keys();

            while (emotions.hasNext()) {
                String emotion = emotions.next();
                Double emotionDbl = (Double) emotionsJson.get(emotion);
                if (emotionDbl > maxEmotionDbl) {
                    maxEmotionDbl = emotionDbl;
                    maxEmotion = emotion;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return maxEmotion;
    }
}
