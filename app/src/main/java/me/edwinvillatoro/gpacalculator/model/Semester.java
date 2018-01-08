package me.edwinvillatoro.gpacalculator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edwin Villatoro on 1/2/2018.
 */
public class Semester {

    private String name;
    private double qualityPoints;
    private double credits;
    private List<Course> courseList;

    public Semester(String semesterName) {
        this.name = semesterName;
        this.qualityPoints = 0;
        this.credits = 0;
        this.courseList = new ArrayList<>();
    }

    public void addCourse(Course newCourse) {
        if (newCourse == null) {
            throw new IllegalArgumentException("course is null");
        }

        this.courseList.add(0,newCourse);

        //TODO: fix after changing what is stored in database
        if (newCourse.getGrade() != -1) {
            this.qualityPoints += newCourse.getQualityPoints();
            this.credits += newCourse.getCredits();
        }
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
        if (this.credits == 0) {
            return 0;
        } else {
            return this.qualityPoints / this.credits;
        }
    }

    public List<Course> getCourseList() {
        return courseList;
    }
}
