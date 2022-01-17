package com.main;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JSON
public class Student {
    private int age;
    // @JSON("fullname")
    private String name;
    private String email;
    private String groupName;
}
