package org.example;


public class StudentTest {
    public static void main(String[] args) {
        StudentManager s1 = new StudentManager("Rahim", 80, 75, 70,new ExactGrade());
        StudentManager s2 = new StudentManager("Karim", 30, 40, 20,new PassFail() );

        s1.showResult();
        s2.showResult();

        StudentRegistry.getInstance().showStudents();
    }
}
