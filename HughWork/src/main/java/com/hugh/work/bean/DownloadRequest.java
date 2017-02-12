package com.hugh.work.bean;

/**
 * Created by Hugh on 2016/8/31.
 */
public class DownloadRequest extends RequestListBean {

    private String filePath;
    private String fileName;
    public DownloadRequest(int requestType) {
        super(requestType);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }



}
