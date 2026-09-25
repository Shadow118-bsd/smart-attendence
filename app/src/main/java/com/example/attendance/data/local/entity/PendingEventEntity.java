package com.example.attendance.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pending_events")
public class PendingEventEntity {

    @PrimaryKey
    @NonNull
    private String eventId; // UUID

    private String eventType; // CHECK_IN, CHECK_OUT, HEARTBEAT
    private String payloadJson; // JSON chứa request payload
    private String timestamp;
    private String status; // PENDING, SYNCING, SYNCED, FAILED
    private int retryCount;

    public PendingEventEntity(@NonNull String eventId, String eventType, String payloadJson, String timestamp, String status, int retryCount) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.payloadJson = payloadJson;
        this.timestamp = timestamp;
        this.status = status;
        this.retryCount = retryCount;
    }

    @NonNull
    public String getEventId() { return eventId; }
    public void setEventId(@NonNull String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
}
