package com.example.skillswapcampus;

public class SkillItem {
    public String skillId;
    public String title;
    public String description;
    public String category;
    public String ownerName;
    public String ownerUid;

    // חובה ל-Firebase
    public SkillItem() {
    }

    public SkillItem(String skillId, String title, String description, String category,
                     String ownerName, String ownerUid) {
        this.skillId = skillId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.ownerName = ownerName;
        this.ownerUid = ownerUid;
    }
}
