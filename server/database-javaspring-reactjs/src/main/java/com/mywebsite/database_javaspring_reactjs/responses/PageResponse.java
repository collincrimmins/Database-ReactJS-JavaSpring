package com.mywebsite.database_javaspring_reactjs.responses;

import java.util.List;

import lombok.Data;

@Data
public class PageResponse<T> {
    private List<T> content;
    
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private Long totalElements;
}
