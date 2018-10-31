package com.zhy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author zhy Excel读取工具类
 */
public class ExcelReadUtils {

    // 标题行
    private static final int HEADER = 0;

    // 正文
    private static final int START = 1;

    private Workbook book;

    /**
     * 在表单中自己需要的字段
     * key:excel对应标题 ,value:对象属性
     */
    private Map<String, String> mapper;
    /**
     * 装换失败的数据信息，记录行数
     */
    private StringBuffer error = new StringBuffer(0);


    /**
     * Excel的头信息
     */
    private Map<Integer, String> header;
    /**
     * 默认的日期格式
     */
    private String date_format = "yyyy-MM-dd";


    private SimpleDateFormat format;

    // 读文件
    private InputStream is;

    /**
     * 初始化工作簿
     *
     * @param pathName
     */
    public void init(String pathName) {

        try {
            is = new FileInputStream(pathName);
            if(pathName.endsWith(".xls")) {
                book = new HSSFWorkbook(is);
            } else {
                book = new XSSFWorkbook(is);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    /**
     * 初始化工作簿 用于实现web上传
     *
     * @param
     */
//    public void init(MultipartFile file, String fileName) {
//
//        try {
//            is = file.getInputStream();
//            if(fileName.endsWith(".xls")) {
//                book = new HSSFWorkbook(is);
//            } else {
//                book = new XSSFWorkbook(is);
//            }
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }

    public ExcelReadUtils(Map<String, String> mapper) {
        this.mapper = mapper;
        format = new SimpleDateFormat(date_format);
    }


    public ExcelReadUtils(Map<String, String> mapper, String date_format) {
        this.mapper = mapper;
        this.date_format = date_format;
        format = new SimpleDateFormat(date_format);
    }


    /**
     *
     * @return true 存在错误，false 不存在错误
     */
    public boolean hasError() {
        return error.capacity() > 0;
    }


    public StringBuffer getError() {
        return error;
    }


    /**
     * 获取第一行标题栏数据
     *
     * @param sheet
     * @return map key：标题栏列下标（0开始） value 标题栏值
     */
    private void loadHeader(Sheet sheet) {
        this.header = new HashMap<Integer, String>();
        Row row = sheet.getRow(HEADER);
        int columns = row.getLastCellNum();
        for (int i = 0; i < columns; i++) {
            String value = row.getCell(i).getStringCellValue();
            if (null == value) {
                throw new RuntimeException("标题栏不能为空！");
            }
            header.put(i, value);
        }
    }


    /**
     *
     * @param clazz
     * @return
     */
    public <T> List<T> bindToModels(Class clazz) throws Exception {
        // 获取第一页
        Sheet sheet = this.book.getSheetAt(0);
        // 获取行数
//		int rowNum = sheet.getLastRowNum();
        int rowNum = getRealRowNum(sheet);
//        System.out.println(rowNum);
        if (rowNum < 1) {
            return new ArrayList<T>();
        }
        // 加载标题栏数据
        this.loadHeader(sheet);
        List<T> result = new ArrayList<T>();

        for (int i = START; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            int cellNum = row.getPhysicalNumberOfCells();
            T instance = (T) clazz.newInstance();
            for (int columns = 0; columns <= cellNum; columns++) {
                Cell cell = row.getCell(columns);
                // 判断单元格的数据类型,转换成String
                String cellValue = loadCellType(cell);
                // 获取单元格的值
                if (cellValue != null) {
                    // 去空白字符
                    cellValue = cellValue.replaceAll("\\s*", "");
                    String key = header.get(columns);
                    // 加载实际值
                    if (this.mapper.containsKey(key)) {
                        this.loadValue(clazz, instance, this.mapper.get(key), cellValue);
                    }
                }
            }
            // 去掉类中所有属性为null的对象
            if (!AllFieldIsNull.allFieldIsNull(instance)) {
                result.add(instance);
            }
        }
        return result;
    }


    /**
     * 将单元格数据转换成String类型
     *
     * @param cell
     * @return
     */
    private String loadCellType(Cell cell) {
        String value = null;
        if(null != cell) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    // 判断当前的cell是否为Date
                    if (DateUtil.isCellDateFormatted(cell)) {
                        value = this.formateDate(cell.getDateCellValue());
                    } else {
                        value = String.valueOf((long) cell.getNumericCellValue());
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    System.out.println("不支持函数！");
                    break;
            }
        }

        return value;
    }

    /**
     * 真实的行数
     */
    private int getRealRowNum(Sheet sheet) {
        // 行数
        int endRowNum = sheet.getPhysicalNumberOfRows();

        int realRows = endRowNum;
        for (int i = 0; i < endRowNum; i++) {
            if(null == sheet.getRow(i)) {
                // 列数
                int columns = sheet.getRow(i).getLastCellNum();
                for (int j = 0; j < columns; j++) {
                    if ( null == sheet.getRow(i).getCell(j)) {
                        return realRows = j + 1;
                    }
                }
            }

        }
        return realRows;
    }

    /**
     * 真实的列数
     */
    private int getRealColsNum(Sheet sheet) {
        return 0;
    }

    /**
     * 注入属性值
     *
     * @param instance
     * @param pro 属性对象
     * @param value 属性值
     */
    @SuppressWarnings("unchecked")
    private <T> void loadValue(Class clazz, T instance, String pro, String value) throws SecurityException, NoSuchMethodException, Exception {
        String getMethod = this.initGetMethod(pro);
        Class type = clazz.getDeclaredMethod(getMethod, null).getReturnType();

        Method method = clazz.getMethod(this.initSetMethod(pro), type);

        if (type == String.class) {
            method.invoke(instance, value);
        } else if (type == int.class || type == Integer.class) {
            method.invoke(instance, Integer.parseInt(value));
        } else if (type == long.class || type == Long.class) {
            method.invoke(instance, Long.parseLong(value));
        } else if (type == float.class || type == Float.class) {
            method.invoke(instance, Float.parseFloat(value));
        } else if (type == double.class || type == Double.class) {
            method.invoke(instance, Double.parseDouble(value));
        } else if (type == Date.class) {
            method.invoke(instance, this.parseDate(value));
        }

    }


    private Date parseDate(String value) throws ParseException {
        return format.parse(value);
    }

    private String formateDate(Date date) {
        return format.format(date);
    }

    public String initSetMethod(String field) {
        return "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    public String initGetMethod(String field) {
        return "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }


    public String getDate_format() {
        return date_format;
    }


    public void setDate_format(String date_format) {
        this.date_format = date_format;
    }

    public void close() {
        try {
            this.is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
