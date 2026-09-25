package com.example.attendance.data.model.request;

public class FaceEvidence {
    private boolean verified;
    private boolean livenessPassed;

    public FaceEvidence(boolean verified, boolean livenessPassed) {
        this.verified = verified;
        this.livenessPassed = livenessPassed;
    }

    public boolean isVerified() { return verified; }
    public boolean isLivenessPassed() { return livenessPassed; }
}
