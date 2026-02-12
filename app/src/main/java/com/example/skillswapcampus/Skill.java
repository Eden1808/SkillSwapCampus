package com.example.skillswapcampus;

public class Skill {

    public String title;
    public String description;
    public String category;

    public Skill() {
        // חובה בשביל Firebase
    }

    public Skill(String title, String description, String category) {
        this.title = title;
        this.description = description;
        this.category = category;
    }
}
