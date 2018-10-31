package com.zhy;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // 原数据
        File file = new File("/Users/zhy/Desktop/20180702/20170922171972_00125406.DAT");
        HashMap<String, String> map = FileRead.txt2String(file);

        Map<String, String> mapper = new HashMap<String, String>();
        mapper.put("序号", "orderNum");
        mapper.put("证件类型", "identityType");
        mapper.put("证件号码（18或15)", "identityNum");
        mapper.put("客户中文名", "name");
        mapper.put("手机号码", "mobile");
        mapper.put("地址", "address");
        mapper.put("个人编号", "serial");
        mapper.put("24位卡识别码", "recognition");
        mapper.put("银行卡号", "cardNum");
        mapper.put("银行开户成功标志（1、成功、0失败）", "flag");
        mapper.put("银行开户不成原因", "note");
        ExcelReadUtils excelReadUtils = new ExcelReadUtils(mapper);
        // 需要匹配的数据
        excelReadUtils.init("/Users/zhy/Desktop/20180702/建行8月份开户数据/CB_211102_95533_PN0001082_20170824.xls");
        List<User> users = null;
        try {
            users = excelReadUtils.bindToModels(User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (User user : users) {
            if (map.containsKey(user.getIdentityNum())) {
                user.setCardNum(map.get(user.getIdentityNum()));
                user.setFlag("1");
            } else {
                user.setFlag("0");
                user.setNote("E8435");
            }
        }
        System.out.println(users);

        // 新生成的表格
        File file1 = new File("/Users/zhy/Desktop/20180702/BC_211102_95533_PN0001082_20170824.xlsx");
        OutputStream out = null;
        try {
            out = new FileOutputStream(file1);
            ExcelWriteUtils excelWriteUtils = new ExcelWriteUtils(out);
            List<String> titleName = new ArrayList<>();
            titleName.add("序号");
            titleName.add("证件类型");
            titleName.add("证件号码（18或15)");
            titleName.add("客户中文名");
            titleName.add("手机号码");
            titleName.add("地址");
            titleName.add("个人编号");
            titleName.add("24位卡识别码");
            titleName.add("银行卡号");
            titleName.add("银行开户成功标志（1、成功、0失败）");
            titleName.add("银行开户不成原因");
            List<String> titles = new ArrayList<>();
            titles.add("orderNum");
            titles.add("identityType");
            titles.add("identityNum");
            titles.add("name");
            titles.add("mobile");
            titles.add("address");
            titles.add("serial");
            titles.add("recognition");
            titles.add("cardNum");
            titles.add("flag");
            titles.add("note");
            excelWriteUtils.insertExcel(users, titles, titleName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        // 递归的查找文件
//        TestFile testFile = new TestFile();
//        Set<File> result = new LinkedHashSet<File>(8);
//        File file = new File("F:\\a");
//        try {
//            testFile.doRetrieveMatchingFiles(file, result);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
