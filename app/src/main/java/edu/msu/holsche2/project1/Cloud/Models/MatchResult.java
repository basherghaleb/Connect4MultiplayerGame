package edu.msu.holsche2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "match")
public class MatchResult {
    @Attribute(name = "roomid", required = false)
    private String roomid;

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    @Attribute(name = "host", required = false)
    private boolean host;

    public boolean getHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

    @Attribute(name = "msg", required = false)
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Attribute
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MatchResult() {
    }

    public MatchResult(String status, String msg) {
        this.status = status;
        this.message = msg;
    }
}
