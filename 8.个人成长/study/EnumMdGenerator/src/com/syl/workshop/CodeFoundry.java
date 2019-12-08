package com.syl.workshop;

import com.syl.bean.EnumField;
import com.syl.bean.Field;
import com.syl.util.StringUtils;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码车间
 * 负责准备生成的代码片段
 *
 * @author syl
 * @create 2018-06-22 10:28
 **/
public class CodeFoundry {
    private static final String TRIM_REGEX = "\r|\n|\\s";
    private static final String ILLEGALITY_REGEX = "[^\\w\\$]";
    private List<EnumField> enumList         =  new Vector<>();
    private List<Field>        fieldList        =  new ArrayList<>();
    private String             comment          =  "";
    private String             constructorMethod=  "";
    private List<String>       otherMethod      =  new ArrayList<>();
    private List<String>       annotationList   =  new ArrayList<>();
    private Map<String,String> packagingTypeMap =  new LinkedHashMap<>();
    private Map<String,String> commonTypeMap    =  new LinkedHashMap<>();
    private String[]           regexLib         =  new String[]{
            "^[+-]?\\d{1,9}$",//int
            "^[+-]?\\d{1,18}$",//long
            "^[+-]?\\d+$",//BigInteger
            "^[+-]?\\d?\\.\\d{1,6}$",//float
            "^[+-]?\\d?\\.\\d{1,15}$",//double
            "^[+-]?\\d?\\.\\d+$",//BigDecimal
            "^(true|false)$" //boolean
    };
    /**
     * 每一行markdown table文本
     */
    private String[] eachLine;
    /**
     * 类名称
     */
    private String className;
    private boolean lombok;
    /**
     * 使用包装类
     */
    private boolean packaging;

    public static void test1(String str){
        System.out.println("=============init table data======================");
        System.out.println(str);
        String[] split = str.split("\n");
        CodeFoundry foundry = new CodeFoundry(split, "MyTestEnum", true, true);
        foundry.prepareGenerator();


        System.out.println("=============comment=================");
        System.out.println(foundry.comment);
        System.out.println("=============Constructcomment=================");
        System.out.println(foundry.getConstructComment());

        System.out.println("=============annotation=================");
        for (String anno : foundry.getAnnotationList()) {
            System.out.println(anno);
        }

        System.out.println("=============field======================");
        for (Field field : foundry.getFieldList()) {
            System.out.println(field.getFieldString());
        }
        System.out.println("=============enum======================");
        for (EnumField enumField : foundry.getEnumList()) {
            System.out.println(enumField.getEnumName()+enumField.getEnumContent());
        }
        System.out.println("=============Constructor======================");
        System.out.println(foundry.getConstructorMethod());
        System.out.println("=============otherMethod======================");
        for (String method : foundry.getOtherMethod()) {
            System.out.println(method);
        }
    }

    public static void test2(String str){
        String[] split = str.split("\n");
        CodeFoundry codeFoundry = new CodeFoundry(split, "com.syl.workshop", false, true);
        codeFoundry.prepareGenerator();
    }
    public static void main(String[] args) {
        //BigDecimal
        String str = "header 1 \t value \t titile\n" +
                "NO \t 0\t  否\n" +
                "YES \t 1\t 是";
        test1(str);

    }

    public CodeFoundry(String[] eachLine,String className, boolean lombok, boolean packaging) {
        this.eachLine = eachLine;
        this.className = className;
        this.lombok = lombok;
        this.packaging = packaging;

        commonTypeMap.put("int",regexLib[0]);
        commonTypeMap.put("long",regexLib[1]);
        commonTypeMap.put("java.math.BigInteger",regexLib[2]);
        commonTypeMap.put("float",regexLib[3]);
        commonTypeMap.put("double",regexLib[4]);
        commonTypeMap.put("java.math.BigDecimal",regexLib[5]);
        commonTypeMap.put("boolean",regexLib[6]);
        packagingTypeMap.put("java.lang.Integer",regexLib[0]);
        packagingTypeMap.put("java.lang.Long",regexLib[1]);
        packagingTypeMap.put("java.math.BigInteger",regexLib[2]);
        packagingTypeMap.put("java.lang.Float",regexLib[3]);
        packagingTypeMap.put("java.lang.Double",regexLib[4]);
        packagingTypeMap.put("java.math.BigDecimal",regexLib[5]);
        packagingTypeMap.put("java.lang.Boolean",regexLib[6]);
    }

    /**
     * 准备所有待生成方法
     */
    public void prepareGenerator(){
        prepareComment();
        prepareAnnotation();
        prepareFieldAndEnum();
        prepareConstructor();
        getConstructComment();
        if(!isLombok()){
            prepareGet();
        }
        //prepareGetEnumByX();
    }

    private void prepareComment(){
        StringBuffer sb = new StringBuffer("/*");
        for (int i = 0; i < this.eachLine.length; i++) {
            sb.append(i == 0 ? "" : " ");
            sb.append("*  ").append(eachLine[i]).append("\n");
        }
        sb.append("**/");
        this.comment = sb.toString();
    }

    private void prepareAnnotation() {
        if(isLombok()){
            this.annotationList.add("@lombok.Getter");
        }
    }

    public String getConstructComment(){
        StringBuffer sb = new StringBuffer("/**");
        sb.append("\n");
        sb.append(" * @method ");
        sb.append(this.className).append("\n");
        List<Field> fieldList = this.getFieldList();
        for (int i = 0; i < fieldList.size(); i++) {
            sb.append(" * @param ").append(fieldList.get(i).getName()).append("\n");
        }
        sb.append(" */");
        return sb.toString();
    }

    /**
     * 过滤非法字符
     * @param split
     * @return
     */
    private String[] filterIllegality(String[] split,boolean valid){
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
                   s = s.replaceAll(TRIM_REGEX,"");
            if(valid)s = s.replaceAll(ILLEGALITY_REGEX,"");
            split[i] = s;
        }
        return split;
    }

    /**
     * 准备字段和枚举
     * @return
     */
    private void prepareFieldAndEnum(){
        String[] tableName = filterIllegality(eachLine[0].split("\t"),true);
        for(int i=1;i<eachLine.length;i++){ //从表格的第2行开始生成枚举 所以至少要有2行
            String[] row = filterIllegality(eachLine[i].split("\t"),false);// 获取每一列

            EnumField enumField = new EnumField();
            StringBuffer enumContent=new StringBuffer();
            StringBuffer enumComment=new StringBuffer();
            enumContent.append("( ");
            enumComment.append("/**");
            for (int j = 0; j < row.length; j++) {
                String columnValue = row[j];
                String type = getFieldType(columnValue);
                if(j == 0){//添加枚举 第一列必须为枚举名称
                    enumField.setEnumName(columnValue.toUpperCase().replaceAll(ILLEGALITY_REGEX,""));
                    continue;
                }
                if(fieldList.size() == j-1){ // 从第2列开始生成field 并避免重复
                    String temp = tableName[j];
                    this.fieldList.add(new Field("private",type,StringUtils.underlineToCamel(temp,false)));
                }

                if("String".equals(type)){
                    enumContent.append(" \"").append(columnValue);
                    enumContent.append("\" ");
                }else{
                    enumContent.append(columnValue);
                }
                enumComment.append(columnValue);
                if (row.length - 2 >= 1 && j <= row.length - 2) {
                    enumContent.append(",");
                    enumComment.append("-");
                }
            }
            enumContent.append(")");
            enumComment.append("*/");
            enumField.setEnumContent(enumContent.toString());
            enumField.setEnumComment(enumComment.toString());
            this.enumList.add(enumField);
        }
        //enumList.add(new StringBuffer(";"));
    }

    private void  prepareConstructor(){
        StringBuffer sb = new StringBuffer();
        sb.append("private ");
        sb.append(className).append("(");
        for (Field field : this.fieldList) {
            sb.append(field.getType()).append(" ").append(field.getName()).append(", ");
        }
        sb.delete(sb.length()-2,sb.length());//去掉每个参数之后的2个占位符
        sb.append(")")
        .append("{\n");
        for (Field field : this.fieldList) {
            sb.append("this.").append(field.getName()).append(" = ").append(field.getName()).append(";\n");
        }
        sb.append("}\n");
        this.constructorMethod = sb.toString();
    }

    private void prepareGet(){
        for (Field field : this.fieldList) {
            StringBuffer sb = new StringBuffer();
            sb.append("public ").append(field.getType()).append(" get").append(StringUtils.firstCharUpper(field.getName()))
            .append("(){\n").append("return ").append(field.getName()).append(";\n}\n");
            this.otherMethod.add(sb.toString());
        }
    }

    private void prepareGetEnumByX() {
        for (Field field : fieldList) {
            StringBuffer sb = new StringBuffer();
            String fieldName = field.getName();
            String type = field.getType();
            String fieldUpName = StringUtils.firstCharUpper(fieldName);
            sb.append("public static ").append(className).append(" getEnumBy").append(fieldUpName)
            .append("(").append(field.getType()).append(" ").append(fieldName).append("){").append(className).append("[] values = ").append(className).append(".values();")
            .append("for (").append(className).append(" em : values) {")
                    .append(type).append(" em").append(fieldUpName).append(" = em.get").append(fieldUpName).append("();")
                    .append("if(em").append(fieldUpName).append(" == null)continue;")
                    .append("if(em").append(fieldUpName).append(".equals(").append(fieldName).append("))return em;}return NULL;}");
            this.otherMethod.add(sb.toString());
        }
    }

    /**
     * 依据值返回各数据类型
     * @param value
     * @return
     */
    private String getFieldType(String value){
        Map<String,String> tempTypeMap = isPackaging() ? packagingTypeMap : commonTypeMap;
        for (String key : tempTypeMap.keySet()) {
            Pattern r = Pattern.compile(tempTypeMap.get(key));
            Matcher matcher = r.matcher(value);
            if(matcher.matches()){
                return key;
            }
        }
        return "String";
    }

    public List<EnumField> getEnumList() {
        return enumList;
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public List<String> getOtherMethod() {
        return otherMethod;
    }

    public List<String> getAnnotationList() {
        return annotationList;
    }

    public String getConstructorMethod() {
        return constructorMethod;
    }

    public boolean isLombok() {
        return lombok;
    }

    public boolean isPackaging() {
        return packaging;
    }

    public String getComment() {
        return comment;
    }

    /**
     * 改变默认正则
     * @return
     */
    public CodeFoundry setRegexLib(int index,String newRegex) {
        if(newRegex == null || newRegex.isEmpty())return this;
        regexLib[index] = newRegex;
        return this;
    }

}
