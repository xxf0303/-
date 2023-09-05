package com.example.mvc2.entity;

public class Page {
    //当前页码
    private int current = 1;
    //显示上限
    private int limit = 10;
    //数据总数
    private int rows;
    //查询路径
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    //通过当前页码算出起始行
    public int getOffset(){
        return (current - 1) * limit;
    }
    //获取总页数
    public int getTotal() {
       if(rows % limit == 0) {
           return rows / limit;
       } else {
           return rows / limit + 1;
       }
    }
    public int getFrom() {
       int from = current - 2;
       return from < 1? 1: from;
    }

    public int getTo() {

       int to = current + 2;
       return current > rows?rows:to;
    }
}
