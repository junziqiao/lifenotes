package com.syl.bean;

/**
 * @author syl
 * @create 2018-06-22 15:30
 **/
public class Field {
    private String qualifier;
    private String type;
    private String name;

    public Field(String qualifier, String type, String name) {
        this.qualifier = qualifier;
        this.type = type;
        this.name = name;
    }

    public String getFieldString(){
        return qualifier+" "+type+" "+name+";";
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Field{" +
                "qualifier='" + qualifier + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
