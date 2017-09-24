package memes.memonic;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tarunkhanna on 24/09/17.
 */

public class JSONToMelody extends JSONObject {

    public ArrayList<String> arrEmotions;
    public ArrayList<Integer> arrMelodies;
    public Context context;



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
                // ignore
                e.printStackTrace();
            }
        }

        return maxEmotion;
    }



    // wrapper for each face analyzed
    void findEachMaxEmotion(JSONArray jsonArr) {

        int size = jsonArr.length();
        for (int i = 0; i < size; i++) {
            try {
                arrEmotions.add(findMaxEmotion(jsonArr.getJSONObject(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Context getContext() {
        return context;
    }


    // get melody corresponding to emotion
//    Map<String,Integer> emotionToMelodyHashMap = new HashMap<String,Integer>();
//    emotionToMelodyHashMap.put("anger", MainActivity.ExtraData().R.raw.cw);
//    emotionToMelodyHashMap.put("disgust", R.raw.em);
//    emotionToMelodyHashMap.put("happiness", R.raw.gm);
//    emotionToMelodyHashMap.put("neutral", R.raw.am);
//    emotionToMelodyHashMap.put("sadness", R.raw.dm);
//    emotionToMelodyHashMap.put("surprise", R.raw.crm);




    // create melodies list
    //melodiesArr

//    ArrayList<Integer> createMelodiesArray() {
//        int size = arrEmotions.size();
//
//        for (int i = 0; i < size; i++) {
//            arrMelodies.add(emotionToMelodyHashMap.get(arrEmotions.get(i)));
//        }
//
//        return arrMelodies;
//    }



}
