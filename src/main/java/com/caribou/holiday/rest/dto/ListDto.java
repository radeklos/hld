package com.caribou.holiday.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListDto<T> {

    private List<T> items;

    private int limit;

    private int offset;

    private int total;

    private String next;

    private String previous;

}
