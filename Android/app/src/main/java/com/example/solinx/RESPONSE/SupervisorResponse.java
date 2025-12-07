package com.example.solinx.RESPONSE;

import com.example.solinx.Supervisor;

public class SupervisorResponse {
    private boolean success;
    private String message;
    private Supervisor supervisor;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Supervisor getSupervisor() { return supervisor; }
    public void setSupervisor(Supervisor supervisor) { this.supervisor = supervisor; }
}