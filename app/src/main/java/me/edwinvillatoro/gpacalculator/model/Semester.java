package me.edwinvillatoro.gpacalculator.model;

/**
 * Created by Edwin Villatoro on 1/2/2018.
 */
public class Semester {

    private String name;
    private double qualityPoints;
    private double credits;
    private double gpa;

    public Semester(String semesterName) {
        this.name = semesterName;
        this.qualityPoints = 0;
        this.credits = 0;
        this.gpa = 0;
    }

    public String getName() {
        return name;
    }

    public double getQualityPoints() {
        return qualityPoints;
    }

    public double getCredits() {
        return credits;
    }

    public double getGpa() {
        return gpa;
    }
}
