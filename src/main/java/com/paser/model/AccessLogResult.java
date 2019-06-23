package com.paser.model;

public class AccessLogResult {

    @Override
	public String toString() {
		return "AccessLogResult [ ip = " + ip + ", comment = " + comment + "]";
	}

	private String ip;
    private String comment;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
