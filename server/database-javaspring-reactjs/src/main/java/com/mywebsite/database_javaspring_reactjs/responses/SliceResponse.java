package com.mywebsite.database_javaspring_reactjs.responses;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.Data;

@Data
public class SliceResponse<T> {
    private List<T> content;
    
    private int pageNumber;
    private boolean hasNext;
}
