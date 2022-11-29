package com.address.addressfind.address.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressFindService {
    
    @Value("${juso.openapi.url}")
    private String url;

    @Value("${juso.openapi.confmKey}")
    private String confmKey;

    public String getAddress(String address) throws ParseException{

        String sentence = removeSpecialChar(address);

        String result = getSearchWord(sentence);

        return result;
    }

    private String getOpenApiAddress(String keyword) throws ParseException{
        String result = "";
        RestTemplate restTemplate = new RestTemplate();

        String uri = url + "confmKey=" + confmKey + "&currentPage=1&countPerPage=10&keyword=" + keyword + "&resultType=json&hstryYn=N&firstSort=none";

        String rslt = restTemplate.getForObject(uri, String.class);

        JSONParser jsonParser = new JSONParser();

        JSONObject json = (JSONObject) jsonParser.parse(rslt);
        JSONObject resultJSon = (JSONObject) json.get("results");
        JSONArray jsonArray = (JSONArray) resultJSon.get("juso");

        for(int i = 0; i < jsonArray.size(); i++){
            JSONObject object = (JSONObject) jsonArray.get(i);
            String rn = (String) object.get("rn");
            if(rn.equals(keyword)){
                result = rn;
                return result;
            }
        }

        return result;
    }

    private String getSearchWord(String sentence) throws ParseException{

        String noBlankWord = sentence.replaceAll("\\p{Z}", "");
        String[] word = noBlankWord.split("");
        String result = "";

        for(int i = 0; i < word.length; i++){
            if(i != 0 && i != word.length - 1){
                if(word[i].equals("길")){
                    result = checkJuso(word, i, "길");
                    if(!result.isBlank()){
                        return result;
                    }
                    
                }if(word[i].equals("로")){
                    if(word[i-1].equals("구") && word[i+1].equals("구")){
                        continue;
                    }else if(word[i-1].equals("종") && word[i+1].equals("구")){
                        continue;
                    }else{
                        result = checkJuso(word, i, "로");
                        if(!result.isBlank()){
                            return result;
                        }
                    }
                }
            }
        }

        return result;
    }

    private String checkJuso(String[] word, int idx, String keyword) throws ParseException{
        StringBuilder sb = new StringBuilder();
        sb.append(keyword);
        while(idx != 0){
            idx--;
            sb.append(word[idx]);
            sb.reverse();
            String result = getOpenApiAddress(sb.toString());
            if(!result.isBlank()){
                return result;
            }
            sb.reverse();
        }
        return "";
    }

    private String removeSpecialChar(String address){
        
        String[] specialArr = new String[]{",", "<", ".", ">", "/", "?", "]", "[", "{", "}", "\\", "|", ":", ";", "'", "\"", "~", "`", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "+", "="};

        List<String> specialList = new ArrayList<>();
        specialList = Arrays.asList(specialArr);

        String[] addStr = address.split("");
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < addStr.length; i++){
            if(i == 0 || i == addStr.length - 1){
                if(specialList.contains(addStr[i])){
                    continue;
                }else{
                    sb.append(addStr[i]);
                }
            }else{
                if(specialList.contains(addStr[i])){
                    if(isNumeric(addStr[i-1]) && isNumeric(addStr[i+1])){
                        sb.append(addStr[i]);
                    }else{
                        continue;
                    }
                }else{
                    sb.append(addStr[i]);
                }
            }
        }

        return sb.toString();
    }

    private boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
 
}
