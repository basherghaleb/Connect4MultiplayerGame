package edu.msu.holsche2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "room")
public class StatusResult {
    @Attribute(name = "roomstatus", required = false)
    private String roomstatus;

    public String getRoomStatus() {
        return roomstatus;
    }

    public void setRoomStatus(String stat) {
        this.roomstatus = stat;
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

    public StatusResult() {
    }

}
