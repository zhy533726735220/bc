package com.zhy;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;

public class TestGUI {

    public static void main(final String[] args) {

        final JFrame f = new JFrame("文件匹配");
        f.setLayout(new FlowLayout());
        final JFileChooser fc1 = new JFileChooser();
        final JFileChooser fc2 = new JFileChooser();
        final JLabel l1 = new JLabel("需要匹配的文件");
        final JLabel l2 = new JLabel("源文件");
        final JLabel l3 = new JLabel("需要匹配的文件");
        f.add(l1);
        f.add(l2);
        f.add(l3);
        l3.setVisible(false);
        // 启动多选
        fc1.setMultiSelectionEnabled(true);
        JButton aOpen = new JButton("打开匹配文件");
        JButton bOpen = new JButton("打开源文件");
        JButton cOpen = new JButton("开始匹配");
        f.add(aOpen);
        f.add(bOpen);
        f.add(cOpen);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(800, 300);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        // 匹配文件
        aOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal =  fc1.showOpenDialog(f);
                File[] files = fc1.getSelectedFiles();
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String str = "<html><body> 需要匹配的文件<br>";
                    String str1 = "";
                    for (int i = 0; i < files.length; i++) {
                        str += files[i].getAbsolutePath() + ";<br>";
                        str1 += files[i].getAbsolutePath() + ";";
                    }
                    l1.setText(str + "</body></html>");
                    l3.setText(str1);
                }
            }
        });

        // 源文件
        bOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal =  fc2.showOpenDialog(f);
                File file = fc2.getSelectedFile();
                String str = null;
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    str = file.getAbsolutePath();
                }

                l2.setText(str);
            }
        });

        // 开始匹配
        cOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                HashMap<String, String> map = new HashMap<>();
                // 源文件
                String f2 = l2.getText();
                if (!f2.equals("源文件")) {
                    File file = new File(f2);
                    map = FileRead.txt2String(file);
                }
                // 匹配文件
                String f3 = l3.getText();
                if (!f3.equals("需要匹配的文件")) {
                    String [] arr = f3.split(";");
                    int i = 0;
                    for (String s : arr) {
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
                        excelReadUtils.init(s);
                        List<User> users = null;

                        try {
                            users = excelReadUtils.bindToModels(User.class);
                        } catch (Exception e1) {
                            e1.printStackTrace();
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

                        String [] strings = s.split("\\\\");
                        StringBuffer sb = new StringBuffer();
                        for (int i1 = 0; i1 < strings.length; i1++) {

                            if (i1 == strings.length -1) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

                                sb.append("\\返还文件" + dateFormat.format(new Date())+ "\\" + strings[i1]);
                                continue;
                            }
                            sb.append("\\" + strings[i1]);
                        }
                        String [] strs = sb.toString().split("_");
                        StringBuffer stringBuffer = new StringBuffer();
                        for (String str : strs) {
                            if (str.contains("CB")) {
                                str = str.replace("CB", "BC");
                            }

                            if (str.contains(".")) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                                stringBuffer.append(map.get("institutionNum") + "_" + dateFormat.format(new Date()));
                                stringBuffer.append(".xlsx");
                                break;
                            }
                            stringBuffer.append(str + "_");
                        }

                        // 新生成的表格
                        File file1 = new File(stringBuffer.toString());
                        File fileParent = file1.getParentFile();
                        if (!fileParent.exists()) {
                            fileParent.mkdirs();
                        }
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
                        } catch (FileNotFoundException e3) {
                            e3.printStackTrace();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        } finally {
                            try {
                                out.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }



            }
        });

    }

}

