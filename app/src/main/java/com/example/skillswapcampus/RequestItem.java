package com.example.skillswapcampus;

public class RequestItem {
    public String requestId;
    public String ownerName;
    public String ownerUid;
    public String requesterName;
    public String requesterUid;
    public String skillId;
    public String skillTitle;
    public String status;
    public long timestamp;

    public RequestItem() {}

    public RequestItem(String requestId, String ownerName, String ownerUid,
                       String requesterName, String requesterUid,
                       String skillId, String skillTitle,
                       String status, long timestamp) {
        this.requestId = requestId;
        this.ownerName = ownerName;
        this.ownerUid = ownerUid;
        this.requesterName = requesterName;
        this.requesterUid = requesterUid;
        this.skillId = skillId;
        this.skillTitle = skillTitle;
        this.status = status;
        this.timestamp = timestamp;
    }
}
