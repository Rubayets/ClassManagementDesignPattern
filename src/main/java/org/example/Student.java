package org.example;

import java.util.ArrayList;
import java.util.List;

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


interface gradingStrategy {
    String calculateGrade(Student student);
}


class TotalMarks implements MarksOperation {
    @Override
    public double calculate(Student student) {
        return student.getBangla() + student.getEnglish() + student.getMath();
    }
}


class AverageMarks implements MarksOperation {
    @Override
    public double calculate(Student student) {
        return (student.getBangla() + student.getEnglish() + student.getMath()) / 3.0;
    }
}

class ExactGrade implements gradingStrategy {
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


class PassFail implements gradingStrategy {
    private MarksOperation averageMarks = new AverageMarks();

    @Override
    public String calculateGrade(Student student) {
        double marks = averageMarks.calculate(student);
        return marks >= 40 ? "Pass" : "Fail";
    }
}

class StudentRegistry {
    private static StudentRegistry instance = new StudentRegistry();
    private List<Student> students = new ArrayList<>();

    // === Observer List ===
    private List<StudentObserver> observers = new ArrayList<>();

    private StudentRegistry() {}

    public static StudentRegistry getInstance() {
        return instance;
    }

    // Add observer
    public void addObserver(StudentObserver observer) {
        observers.add(observer);
    }

    // Notify observers
    private void notifyObservers(String message) {
        for (StudentObserver o : observers) {
            o.update(message);
        }
    }

    public void addStudent(Student s) {
        students.add(s);
        notifyObservers("New student added: " + s.getName());
    }

    public void showStudents() {
        System.out.println("All Registered Students:");
        for (Student s : students) {
            System.out.println("- " + s.getName());
        }
        System.out.println();
    }

    // iterator
    public StudentIterator iterator() {
        return new StudentListIterator(students);
    }
}

// ===== Iterator Pattern started   =====
// ===== Iterator  interface started  =====
interface StudentIterator {
    boolean hasNext();
    Student next();
}

class StudentListIterator implements StudentIterator {
    private List<Student> students;
    private int index = 0;

    public StudentListIterator(List<Student> students) {
        this.students = students;
    }

    @Override
    public boolean hasNext() {
        return index < students.size();
    }

    @Override
    public Student next() {
        return students.get(index++);
    }
}
// ===== Iterator Pattern End   =====

// ===== Observer Pattern  started =====
interface StudentObserver {
    void update(String message);
}
class LoggerObserver implements StudentObserver {
    @Override
    public void update(String message) {
        System.out.println("[LOG] " + message);
    }
}
// ===== Observer Pattern  end =====


class StudentManager {
    private Student student;
    private MarksOperation totalMarks = new TotalMarks();
    private MarksOperation averageMarks = new AverageMarks();
    private gradingStrategy grade;


    private StudentRegistry registry = StudentRegistry.getInstance();

    public StudentManager(String name, int bangla, int english, int math, gradingStrategy grade) {
        this.student = new Student(name, bangla, english, math);
        this.grade = grade;

        registry.addStudent(student);
    }

    public void showResult() {
        System.out.println("Name: " + student.getName());
        System.out.println("Total: " + totalMarks.calculate(student));
        System.out.println("Average: " + averageMarks.calculate(student));
        System.out.println("Grade: " + grade.calculateGrade(student));
        System.out.println("--------------------");
    }
}
class StudentFacade {
    private StudentRegistry registry = StudentRegistry.getInstance();

    public StudentManager registerStudent(String name, int bangla, int english, int math, gradingStrategy strategy) {
        StudentManager manager = new StudentManager(name, bangla, english, math, strategy);
        return manager;
    }

    public void showResult(StudentManager manager) {
        manager.showResult();
    }

    public void showAllStudents() {
        registry.showStudents();
    }
}
