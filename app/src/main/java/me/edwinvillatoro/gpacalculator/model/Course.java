package me.edwinvillatoro.gpacalculator.model;

/**
 * Created by Edwin Villatoro on 1/2/2018.
 */
public class Course {

    private String name;
    private String semester;
    private double credits;
    private double grade;
    private double qualityPoints;
    private int countTowardsGPA;

    public Course(String name) {
        this(name, "semester", 0,0, 1);
    }

    public Course(String name, String semester, double credits, double grade, int countTowardsGPA) {
        this.name = name;
        this.semester = semester;
        this.credits = credits;
        this.grade = grade;
        this.qualityPoints = credits * grade;
        this.countTowardsGPA = countTowardsGPA;
    }

    @Override
    public String toString() {
        return name + " "  + " " + credits + " " + grade;
    }

    public double getCredits() {
        return credits;
    }

    public double getGrade() {
        return grade;
    }

    public double getQualityPoints() {
        return qualityPoints;
    }

    public String getName() {
        return name;
    }

    public String getSemester() {
        return semester;
    }

    public int getCountTowardsGPA() {
        return countTowardsGPA;
    }
}
