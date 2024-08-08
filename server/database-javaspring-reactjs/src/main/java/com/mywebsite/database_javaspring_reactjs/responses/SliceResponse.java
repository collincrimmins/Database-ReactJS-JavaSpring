package com.mywebsite.database_javaspring_reactjs.responses;

import java.util.List;

import lombok.Data;

@Data
public class SliceResponse<T> {
    private List<T> content;
    
    private Long lastReadRecordID;
    private boolean hasNext;
}
