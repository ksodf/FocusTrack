package main.ui;

public class UItest {

    // Attributes (fields)
    String name;
    int age;
    String major;

    // Constructor
    public UItest(String studentName, int studentAge, String studentMajor) {
        name = studentName;
        age = studentAge;
        major = studentMajor;
    }

    // Method to display student details
    public void displayDetails() {
        System.out.println("=== Student Details ===");
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Major: " + major);
    }

    // Main method to run the program
    public static void main(String[] args) {
        // Create a new student object
        UItest student1 = new UItest("Cappi", 19, "Information and Communication Engineering");

        // Call the method to display the details
        student1.displayDetails();
    }
}