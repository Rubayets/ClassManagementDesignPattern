package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class StudentUI extends JFrame {
    private StudentFacade facade = new StudentFacade();
    private StudentRegistry registry = StudentRegistry.getInstance();

    // Separate table models for each tab
    private DefaultTableModel studentListModel; // Show Students tab
    private DefaultTableModel resultTableModel;  // Show Result tab

    private JTextField nameField, banglaField, englishField, mathField;
    private JComboBox<String> gradingViewComboBox;

    // Tables
    private JTable studentListTable; // Show Students
    private JTable resultTable;      // Show Result

    // Memento
    private List<StudentStateManager> stateManagers = new ArrayList<>();
    private List<StudentMemento> mementos = new ArrayList<>();

    public StudentUI() {
        setTitle("Student Management System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Add Student", addStudentPanel());
        tabbedPane.addTab("Show Students", showStudentsPanel());
        tabbedPane.addTab("Show Result", showResultPanel());
        tabbedPane.addTab("Update Marks", updateMarksPanel());
        tabbedPane.addTab("Reports", reportsPanel());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel addStudentPanel() {
        JPanel panel = new JPanel(new GridLayout(5,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Bangla:"));
        banglaField = new JTextField();
        panel.add(banglaField);

        panel.add(new JLabel("English:"));
        englishField = new JTextField();
        panel.add(englishField);

        panel.add(new JLabel("Math:"));
        mathField = new JTextField();
        panel.add(mathField);

        JButton addBtn = new JButton("Add Student");
        panel.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int b = Integer.parseInt(banglaField.getText());
                int eng = Integer.parseInt(englishField.getText());
                int m = Integer.parseInt(mathField.getText());

                // Default grading strategy is ExactGrade
                gradingStrategy strategy = new ExactGrade();
                StudentManager manager = facade.registerStudent(name,b,eng,m,strategy);

                // Save state for Memento
                StudentStateManager sm = new StudentStateManager(getLastStudent());
                stateManagers.add(sm);
                mementos.add(sm.saveState());

                JOptionPane.showMessageDialog(this, "Student Added Successfully!");
                refreshStudentListTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric marks!");
            }
        });

        return panel;
    }

    private JPanel showStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        studentListModel = new DefaultTableModel(new String[]{"Name","Bangla","English","Math"},0);
        studentListTable = new JTable(studentListModel);
        JScrollPane scrollPane = new JScrollPane(studentListTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        panel.add(refreshBtn, BorderLayout.SOUTH);
        refreshBtn.addActionListener(e -> refreshStudentListTable());

        return panel;
    }

    private JPanel showResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        resultTableModel = new DefaultTableModel(new String[]{"Name","Total","Average","Grade"},0);
        resultTable = new JTable(resultTableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        gradingViewComboBox = new JComboBox<>(new String[]{"Exact Grade", "Pass/Fail"});
        panel.add(gradingViewComboBox, BorderLayout.NORTH);

        JButton showBtn = new JButton("Show Result");
        panel.add(showBtn, BorderLayout.SOUTH);
        showBtn.addActionListener(e -> showResults());

        return panel;
    }

    private JPanel updateMarksPanel() {
        JPanel panel = new JPanel(new GridLayout(5,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        panel.add(new JLabel("Select Student:"));
        JComboBox<String> studentCombo = new JComboBox<>();
        panel.add(studentCombo);

        panel.add(new JLabel("Bangla:"));
        JTextField bangla = new JTextField();
        panel.add(bangla);

        panel.add(new JLabel("English:"));
        JTextField english = new JTextField();
        panel.add(english);

        panel.add(new JLabel("Math:"));
        JTextField math = new JTextField();
        panel.add(math);

        JButton updateBtn = new JButton("Update Marks");
        panel.add(updateBtn);

        JButton restoreBtn = new JButton("Restore Marks");
        panel.add(restoreBtn);

        updateBtn.addActionListener(e -> {
            int index = studentCombo.getSelectedIndex();
            if(index<0) return;
            Student student = getStudentAt(index);
            try {
                int b = Integer.parseInt(bangla.getText());
                int eng = Integer.parseInt(english.getText());
                int m = Integer.parseInt(math.getText());

                StudentMemento newState = new StudentMemento(b,eng,m);
                mementos.set(index,newState);

                StudentStateManager sm = stateManagers.get(index);
                sm.restoreState(newState);

                JOptionPane.showMessageDialog(this,"Marks Updated!");
                refreshStudentListTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric marks!");
            }
        });

        restoreBtn.addActionListener(e -> {
            int index = studentCombo.getSelectedIndex();
            if(index<0) return;

            StudentStateManager sm = stateManagers.get(index);
            StudentMemento mem = mementos.get(index);
            sm.restoreState(mem);
            JOptionPane.showMessageDialog(this,"Marks Restored!");
            refreshStudentListTable();
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent ce) {
                studentCombo.removeAllItems();
                StudentIterator it = registry.iterator();
                while(it.hasNext()) studentCombo.addItem(it.next().getName());
            }
        });

        return panel;
    }

    private JPanel reportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JButton reportBtn = new JButton("Generate Report");
        JButton highestBtn = new JButton("Show Highest Total");
        JButton topperBtn = new JButton("Show Topper");

        JPanel btnPanel = new JPanel();
        btnPanel.add(reportBtn);
        btnPanel.add(highestBtn);
        btnPanel.add(topperBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        reportBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            StudentIterator it = registry.iterator();
            while(it.hasNext()) {
                Student s = it.next();
                StudentReportAdapter adapter = new StudentReportAdapter(s);
                sb.append(adapter.getReport()).append("\n");
            }
            reportArea.setText(sb.toString());
        });

        highestBtn.addActionListener(e -> {
            StudentIterator it = registry.iterator();
            Student highest = null;
            double max = -1;
            while(it.hasNext()) {
                Student s = it.next();
                double total = new TotalMarks().calculate(s);
                if(total>max) {
                    max = total;
                    highest = s;
                }
            }
            reportArea.setText("Highest Total Marks: " + highest.getName() + " = " + max);
        });

        topperBtn.addActionListener(e -> {
            StudentIterator it = registry.iterator();
            Student topper = null;
            double maxAvg = -1;
            while(it.hasNext()) {
                Student s = it.next();
                double avg = new AverageMarks().calculate(s);
                if(avg>maxAvg) {
                    maxAvg = avg;
                    topper = s;
                }
            }
            reportArea.setText("Topper: " + topper.getName() + " with Average: " + maxAvg);
        });

        return panel;
    }

    private Student getLastStudent() {
        StudentIterator it = registry.iterator();
        Student last = null;
        while(it.hasNext()) last = it.next();
        return last;
    }

    private Student getStudentAt(int index) {
        StudentIterator it = registry.iterator();
        int i = 0;
        while(it.hasNext()) {
            Student s = it.next();
            if(i==index) return s;
            i++;
        }
        return null;
    }

    private void refreshStudentListTable() {
        studentListModel.setRowCount(0);
        StudentIterator it = registry.iterator();
        while(it.hasNext()) {
            Student s = it.next();
            studentListModel.addRow(new Object[]{s.getName(), s.getBangla(), s.getEnglish(), s.getMath()});
        }
    }

    private void showResults() {
        resultTableModel.setRowCount(0);
        String view = (String) gradingViewComboBox.getSelectedItem();
        StudentIterator it = registry.iterator();
        while(it.hasNext()) {
            Student s = it.next();
            double total = new TotalMarks().calculate(s);
            double avg = new AverageMarks().calculate(s);
            String grade = view.equals("Exact Grade") ? new ExactGrade().calculateGrade(s)
                    : new PassFail().calculateGrade(s);
            resultTableModel.addRow(new Object[]{s.getName(), total, avg, grade});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentUI::new);
    }
}
