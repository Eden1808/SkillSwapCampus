package com.example.skillswapcampus;

public class MatchItem {
    public String skillTitle;
    public String withName;
    public String roleText; // "You requested" / "They requested"

    public MatchItem() {}

    public MatchItem(String skillTitle, String withName, String roleText) {
        this.skillTitle = skillTitle;
        this.withName = withName;
        this.roleText = roleText;
    }
}
