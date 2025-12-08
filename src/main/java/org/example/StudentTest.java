package org.example;

public class StudentTest {
    public static void main(String[] args) {
        StudentFacade facade = new StudentFacade();

        StudentManager s1 = facade.registerStudent("Rahim", 85, 75, 90, new ExactGrade());
        StudentManager s2 = facade.registerStudent("Karim", 40, 35, 50, new PassFail());


        facade.showResult(s1);
        facade.showResult(s2);

        
        facade.showAllStudents();
    }
}
