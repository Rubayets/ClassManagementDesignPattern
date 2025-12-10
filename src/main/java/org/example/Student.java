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
    public void setBangla(int bangla) { this.bangla = bangla; }
    public void setEnglish(int english) { this.english = english; }
    public void setMath(int math) { this.math = math; }


    public String getName() { return name; }
    public int getBangla() { return bangla; }
    public int getEnglish() { return english; }
    public int getMath() { return math; }
}

// Strategy Pattern

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


// Simple Factory Pattern Added

class StrategyFactory {

    // Create Grading Strategy
    public static gradingStrategy getGradingStrategy(String type) {
        switch (type.toLowerCase()) {
            case "exact":
                return new ExactGrade();
            case "passfail":
                return new PassFail();
            default:
                throw new IllegalArgumentException("Unknown grading strategy: " + type);
        }
    }

    // Create Marks Operation Strategy
    public static MarksOperation getMarksOperation(String type) {
        switch (type.toLowerCase()) {
            case "total":
                return new TotalMarks();
            case "average":
                return new AverageMarks();
            default:
                throw new IllegalArgumentException("Unknown marks operation: " + type);
        }
    }
}


// Singleton Pattern

class StudentRegistry {
    private static StudentRegistry instance = new StudentRegistry();
    private List<Student> students = new ArrayList<>();

    private List<StudentObserver> observers = new ArrayList<>();

    private StudentRegistry() {}

    public static StudentRegistry getInstance() { return instance; }

    public void addObserver(StudentObserver observer) {
        observers.add(observer);
    }

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

    public StudentIterator iterator() {
        return new StudentListIterator(students);
    }
}

// Iterator Pattern

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

// Observer Pattern

interface StudentObserver {
    void update(String message);
}

class LoggerObserver implements StudentObserver {
    @Override
    public void update(String message) {
        System.out.println("[LOG] " + message);
    }
}

// Command Pattern

interface Command {
    void execute();
}

class AddStudentCommand implements Command {
    private StudentFacade facade;
    private String name;
    private int b, e, m;
    private gradingStrategy strategy;

    public AddStudentCommand(StudentFacade facade, String name, int b, int e, int m, gradingStrategy strategy) {
        this.facade = facade;
        this.name = name;
        this.b = b;
        this.e = e;
        this.m = m;
        this.strategy = strategy;
    }

    @Override
    public void execute() {
        facade.registerStudent(name, b, e, m, strategy);
    }
}

class ShowAllStudentsCommand implements Command {
    private StudentFacade facade;

    public ShowAllStudentsCommand(StudentFacade facade) {
        this.facade = facade;
    }

    @Override
    public void execute() {
        facade.showAllStudents();
    }
}

// Composite Pattern

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

interface StudentComponent {
    void showInfo();
}

class StudentLeaf implements StudentComponent {
    private Student student;

    public StudentLeaf(Student student) {
        this.student = student;
    }

    @Override
    public void showInfo() {
        System.out.println("Student: " + student.getName());
    }
}

class StudentGroup implements StudentComponent {
    private String groupName;
    private List<StudentComponent> components = new ArrayList<>();

    public StudentGroup(String groupName) {
        this.groupName = groupName;
    }

    public void add(StudentComponent c) {
        components.add(c);
    }

    @Override
    public void showInfo() {
        System.out.println("Group: " + groupName);
        for (StudentComponent c : components) {
            c.showInfo();
        }
    }
}

// Memento Pattern

class StudentMemento {
    private int b, e, m;

    public StudentMemento(int b, int e, int m) {
        this.b = b;
        this.e = e;
        this.m = m;
    }

    public int getBangla() { return b; }
    public int getEnglish() { return e; }
    public int getMath() { return m; }
}

class StudentStateManager {
    private Student student;

    public StudentStateManager(Student student) {
        this.student = student;
    }

    public StudentMemento saveState() {
        return new StudentMemento(student.getBangla(), student.getEnglish(), student.getMath());
    }

    public void restoreState(StudentMemento m) {
        try {
            var b = student.getClass().getDeclaredField("bangla");
            var e = student.getClass().getDeclaredField("english");
            var mt = student.getClass().getDeclaredField("math");

            b.setAccessible(true);
            e.setAccessible(true);
            mt.setAccessible(true);

            b.set(student, m.getBangla());
            e.set(student, m.getEnglish());
            mt.set(student, m.getMath());
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}

class StudentHistory {
    private List<StudentMemento> list = new ArrayList<>();
    public void save(StudentMemento m) { list.add(m); }
    public StudentMemento get(int i) { return list.get(i); }
}

// Adapter Pattern

interface ReportFormat {
    String getReport();
}

class StudentReportAdapter implements ReportFormat {
    private Student student;

    public StudentReportAdapter(Student student) {
        this.student = student;
    }

    @Override
    public String getReport() {
        return "Report => Name: " + student.getName() +
                ", Bangla: " + student.getBangla() +
                ", English: " + student.getEnglish() +
                ", Math: " + student.getMath();
    }
}
