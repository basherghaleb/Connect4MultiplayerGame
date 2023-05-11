package edu.msu.holsche2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "login")
public class LoginResult {
    @Attribute(name = "msg", required = false)
    private String message;
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Attribute
    private String status;
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Attribute(name = "userId", required = false)
    private int userId;
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LoginResult() {}

    public LoginResult(String msg, String status, int userId) {
        this.message = msg;
        this.status = status;
        this.userId = userId;
    }
}
