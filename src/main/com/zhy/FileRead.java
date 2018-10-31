package com.zhy;

import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileRead {
    /**
     * 读取txt文件的内容
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public static HashMap<String, String> txt2String(File file){
        StringBuilder result = new StringBuilder();
        try{
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br = new BufferedReader(reader);//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        String [] arr = result.toString().split("\\s+");
        User vo = new User();
        Map<String, String> map = new HashMap<>();
        for(String ss : arr){

            if (ss.length() == 69) {
                String institutionNum = ss.substring(17, 26);
                String cardNum = ss.substring(47, 66);
                vo.setCardNum(cardNum);
                vo.setInstitutionNum(institutionNum);
            }
            if (ss.length() == 24) {
                String identityNum = ss.substring(6, 24);
                vo.setIdentityNum(identityNum);
            }
            if (ss.contains("联复合盘锦社")) {
                map.put(vo.getIdentityNum(), vo.getCardNum());
            }
        }
        map.put("institutionNum", vo.getInstitutionNum());
        return (HashMap<String, String>) map;
    }



}
