package com.example.dbbackuppcloud.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Notification {
    private List<Integer> receivers;
    private String message;
}
