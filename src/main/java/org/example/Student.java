package org.example;

public class Student {
    private String name;
    private int bangla;
    private int english;
    private int math;

    public Student(String name, int bangla, int english, int math) {
        this.name = name;
        this.bangla = bangla;
        this.english = english;
        this.math = math;
    }
    public Student(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public int getMath()
    {
        return math;
    }
    public int getBangla()
    {
        return bangla;
    }
    public int getEnglish()
    {
        return english;
    }


    public int totalMarks() {
        return bangla + english + math;
    }

    public double averageMarks() {
        return totalMarks() / 3.0;
    }

    public String getGrade() {
        double marks = averageMarks();
        if (marks >= 80.0) return "A+";
        else if (marks >= 70.0) return "A";
        else if (marks >= 60.0) return "B";
        else if (marks >= 50.0) return "C";
        else if (marks >= 40.0) return "D";
        else return "F";
    }
}
