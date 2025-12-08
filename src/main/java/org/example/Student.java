package org.example;

class Student {
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

    public String getName() { return name; }
    public int getBangla() { return bangla; }
    public int getEnglish() { return english; }
    public int getMath() { return math; }
}


interface MarksOperation {
    double calculate(Student student);
}


interface GradingStrategy {
    String calculateGrade(Student student);
}


class TotalMarks implements MarksOperation {
    @Override
    public double calculate(Student student) {
        return student.getBangla() + student.getEnglish() + student.getMath();
    }
}

// Average marks calculation
class AverageMarks implements MarksOperation {
    @Override
    public double calculate(Student student) {
        return (student.getBangla() + student.getEnglish() + student.getMath()) / 3.0;
    }
}

class ExactGrade implements GradingStrategy {
    private MarksOperation averageMarks = new AverageMarks();

    @Override
    public String calculateGrade(Student student) {
        double marks = averageMarks.calculate(student);
        if (marks >= 80.0) return "A+";
        else if (marks >= 70.0) return "A";
        else if (marks >= 60.0) return "B";
        else if (marks >= 50.0) return "C";
        else if (marks >= 40.0) return "D";
        else return "F";
    }
}


class PassFail implements GradingStrategy {
    private MarksOperation averageMarks = new AverageMarks();

    @Override
    public String calculateGrade(Student student) {
        double marks = averageMarks.calculate(student);
        return marks >= 40 ? "Pass" : "Fail";
    }
}


class StudentManager {
    private Student student;
    private MarksOperation totalMarks = new TotalMarks();
    private MarksOperation averageMarks = new AverageMarks();
    private GradingStrategy grade;

    public StudentManager(String name, int bangla, int english, int math, GradingStrategy grade) {
        this.student = new Student(name, bangla, english, math);
        this.grade = grade;
    }

    public void showResult() {
        System.out.println("Name: " + student.getName());
        System.out.println("Total: " + totalMarks.calculate(student));
        System.out.println("Average: " + averageMarks.calculate(student));
        System.out.println("Grade: " + grade.calculateGrade(student));
        System.out.println("--------------------");
    }
}
