package com.example.pc_asus.tinhnguyenvien.Model;

public class Status {
    public String statusWithFriends;
    public String statusWithAll;
    public String connectionRequest;

    public Status() {
    }

    public Status(String statusWithFriends, String statusWithAll, String connectionRequest) {
        this.statusWithFriends = statusWithFriends;
        this.statusWithAll = statusWithAll;
        this.connectionRequest = connectionRequest;
    }
}
