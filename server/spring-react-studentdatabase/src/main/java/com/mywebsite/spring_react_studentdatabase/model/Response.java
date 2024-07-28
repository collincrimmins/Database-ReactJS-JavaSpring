package com.mywebsite.spring_react_studentdatabase.model;

import java.util.List;

import lombok.Data;

@Data
public class Response<T> {
    private List<T> content;
    
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
