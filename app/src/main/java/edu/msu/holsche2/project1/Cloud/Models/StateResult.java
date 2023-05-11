package edu.msu.holsche2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "state")
public class StateResult{
    @Attribute(name = "state", required = false)
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Attribute(name = "msg", required = false)
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Attribute(name = "host", required = false)
    private String host;

    public String getHost() { return host; }

    @Attribute(name = "guest", required = false)
    private String guest;

    public String getGuest() { return guest; }

    @Attribute(name = "roomstatus", required = false)
    private String roomStatus;

    public String getRoomStatus() { return roomStatus; }

    @Attribute
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public StateResult() {
    }
}
