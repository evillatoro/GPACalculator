package me.edwinvillatoro.gpacalculator.model;

/**
 * Created by Edwin Villatoro on 1/4/2018.
 */
public class Grade {

    private String letter;
    private double points;

    public Grade(String letter, double points) {
        this.letter = letter;
        this.points = points;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return letter + " " + points;
    }
}