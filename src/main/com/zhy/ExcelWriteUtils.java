package com.zhy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author zhy Excel写入工具类
 */
public class ExcelWriteUtils {
    // 写文件
    private OutputStream out;
    private Workbook book;
    private InputStream is;
    private Sheet sheet;
    private String pathName;
    //Float类型数据小数位
    private String floatDecimal = ".00";
    //Double类型数据小数位
    private String doubleDecimal = ".00";

    private DecimalFormat floatDecimalFormat=new DecimalFormat(floatDecimal);
    private DecimalFormat doubleDecimalFormat=new DecimalFormat(doubleDecimal);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     *
     * @param out web下载时用到的
     * @throws IOException
     */
//    public ExcelWriteUtils(ServletOutputStream out) throws IOException {
//        book = new XSSFWorkbook();
//        this.out = out;
//    }

    /**
     * 写入Excel（.xlsx格式）
     * @param out
     * @throws IOException
     */
     public ExcelWriteUtils(OutputStream out) throws IOException {
        book = new XSSFWorkbook();
        this.out = out;
     }

    /**
     * 设置float类型数据小数位 默认.00
     * @param doubleDecimal 如 ".00"
     */
    public void setDoubleDecimal(String doubleDecimal) {
        this.doubleDecimal = doubleDecimal;
    }
    /**
     * 设置doubel类型数据小数位 默认.00
     * @param floatDecimalFormat 如 ".00
     */
    public void setFloatDecimalFormat(DecimalFormat floatDecimalFormat) {
        this.floatDecimalFormat = floatDecimalFormat;
    }

    /**
     * 写入excel表格
     * @param datas list集合
     * @param titles 对应bean的属性名
     * @param titleName excel对应的字段名
     * @throws Exception
     */
    public void insertExcel(List<?> datas, List<String> titles, List<String> titleName) throws Exception {
        //写入excel的表头
        Row titleNameRow = book.createSheet("工作表2").createRow(0);
        for(int i = 0;i < titleName.size();i++){
            Cell cell = titleNameRow.createCell(i);
            cell.setCellValue(titleName.get(i).toString());
        }
        if(datas != null && datas.size() >0) {
            for (int rowIndex = 1; rowIndex <= datas.size(); rowIndex++) {
                // 获得该对象
                Object obj = datas.get(rowIndex -1);
                // 获得该对象的class实例
                Class clazz = obj.getClass();
                Row row = book.getSheet("工作表2").createRow(rowIndex);
                for (int columnIndex = 0; columnIndex < titles.size(); columnIndex++) {
                    String title = titles.get(columnIndex).trim();
                    // 如果字段不为空
                    if(!"".equals(title)) {
                        //使首字母大写
                        String UTitle = Character.toUpperCase(title.charAt(0))+ title.substring(1, title.length());
                        String methodName  = "get" + UTitle;
                        // 设置要执行的方法
                        Method method = clazz.getDeclaredMethod(methodName);
                        //获取返回类型
                        String returnType = method.getReturnType().getName();
                        Cell cell = row.createCell(columnIndex);

                        String data = method.invoke(obj) == null ? "" : method.invoke(obj).toString();

                        if(data!=null&&!"".equals(data)){
                            if("int".equals(returnType)){
                                cell.setCellValue(Integer.parseInt(data));
                            }else if("long".equals(returnType)){
                                cell.setCellValue(Long.parseLong(data));
                            }else if("float".equals(returnType)){
                                cell.setCellValue(floatDecimalFormat.format(Float.parseFloat(data)));
                            }else if("double".equals(returnType)){
                                cell.setCellValue(doubleDecimalFormat.format(Double.parseDouble(data)));
                            }else if("java.util.Date".equals(returnType)) {
                                if(method.invoke(obj) != null){
                                    Date date = (Date)method.invoke(obj);
                                    cell.setCellValue(dateFormat.format(date));
                                }else{
                                    cell.setCellValue("");
                                }
                            }else{
                                cell.setCellValue(data);
                            }
                        }

                    }

                }
            }
        }
        book.write(out);
        out.close();
    }

    /**
     *
     * @param datas
     * @param titleName excel 字段名字
     * @throws Exception
     */
    public void insertExcel(List<String> datas, List<String> titleName, int postion) throws Exception {
        //写入excel的表头
        Row titleNameRow = book.createSheet("工作表2").createRow(0);
        for(int i = 0;i < titleName.size();i++){
            Cell cell = titleNameRow.createCell(i);
            cell.setCellValue(titleName.get(i).toString());
        }
        if(datas != null && datas.size() >0) {
            for (int rowIndex = 1; rowIndex <= datas.size(); rowIndex++) {

                Row row = book.getSheet("工作表2").createRow(rowIndex);
                Cell cell = row.createCell(postion);
                // 获得值
                String data = datas.get(rowIndex -1);
                if(data == null) {
                    continue;
                }
                cell.setCellValue(data);
            }
        }
        book.write(out);
        out.close();

    }

}
