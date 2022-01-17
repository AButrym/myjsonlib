package com.main;

public class App {
    public static void main(String[] args) {
        // POJO - Plain Old Java Object
        Student student1 = Student.builder()
                .name("Petro")
                .groupName("AAA-1")
                .email("petro@gmail.com")
                .age(22)
                .build();
        Student student2 = new Student(
                22, "Petro", "petro@gmail.com", "AAA-1");

        String jsonStudent1 = JsonUtil.stringify(student1);
        String jsonStudent2 = JsonUtil.stringify(student2);
        System.out.println(jsonStudent1);
        System.out.println(jsonStudent1.equals(jsonStudent2));

        Student student3 = JsonUtil.parse(jsonStudent1, Student.class);
        System.out.println(student3);
        System.out.println(student3.equals(student1));
    }
}
