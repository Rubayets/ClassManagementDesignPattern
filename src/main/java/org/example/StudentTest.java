package org.example;

public class StudentTest {
    public static void main(String[] args) {
        StudentFacade facade = new StudentFacade();
        // Register Observer (Logger)
        StudentRegistry.getInstance().addObserver(new LoggerObserver());

        StudentManager s1 = facade.registerStudent("Rahim", 85, 75, 90, new ExactGrade());
        StudentManager s2 = facade.registerStudent("Karim", 40, 35, 50, new PassFail());

        facade.showResult(s1);
        facade.showResult(s2);

        facade.showAllStudents();
        //command test
        Command add1 = new AddStudentCommand(facade, "Jack", 78, 85, 80, new ExactGrade());
        Command add2 = new AddStudentCommand(facade, "Olivia", 45, 50, 42, new PassFail());

        add1.execute();
        add2.execute();
        System.out.println("After using command pattern");
        facade.showAllStudents();

        //test iterator pattern
        System.out.println("Testing iterator pattern");
        StudentIterator iterator = StudentRegistry.getInstance().iterator();

        while (iterator.hasNext()) {
            Student s = iterator.next();
            System.out.println("Student: " + s.getName());
        }

    }
}