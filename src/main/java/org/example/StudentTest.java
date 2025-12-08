package org.example;

public class StudentTest {
    public static void main(String[] args) {
        StudentFacade facade = new StudentFacade();

        // Register students with different grading strategies
        StudentManager s1 = facade.registerStudent("Rahim", 85, 75, 90, new ExactGrade());
        StudentManager s2 = facade.registerStudent("Karim", 40, 35, 50, new PassFail());

        // Show results via facade
        facade.showResult(s1);
        facade.showResult(s2);

        // Show all students
        facade.showAllStudents();
    }
}
