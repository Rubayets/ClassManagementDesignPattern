package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public void insertStudent(Student s) {
        String sql = "INSERT OR REPLACE INTO students(name, bangla, english, math) VALUES(?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getName());
            ps.setInt(2, s.getBangla());
            ps.setInt(3, s.getEnglish());
            ps.setInt(4, s.getMath());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStudentMarks(Student s) {
        String sql = "UPDATE students SET bangla=?, english=?, math=? WHERE name=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getBangla());
            ps.setInt(2, s.getEnglish());
            ps.setInt(3, s.getMath());
            ps.setString(4, s.getName());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT name, bangla, english, math FROM students";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                int b = rs.getInt("bangla");
                int e = rs.getInt("english");
                int m = rs.getInt("math");

                list.add(new Student(name, b, e, m));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
