package com.alumm0x.collect;

import burp.IHttpRequestResponsePersisted;

public class LogEntry {
    public final int id;
    public final IHttpRequestResponsePersisted requestResponse;
    public final String Hosts;
    public final String Paths;
    public final String Params;
    public final String Files;


    LogEntry(int id, IHttpRequestResponsePersisted requestResponse, String host, String path, String param, String files) {
        this.Files = files;
        this.id = id;
        this.requestResponse = requestResponse;
        this.Params = param;
        this.Paths = path;
        this.Hosts = host;
    }
}
