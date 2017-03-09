package set.work.bean;

import android.os.Handler;

/**
 * Created by Hugh on 2016/8/31.
 * 下载 模型
 */
public class DownloadRequestBean extends RequestListBean implements Cloneable {

    private String filePath;
    private String fileName;
    private String lastModify;
    private int reqID = -1;
    private boolean needVerify;
    private Handler updateHandler;
    public DownloadRequestBean(int requestType) {
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

    public String getLastModify() {
        return lastModify;
    }

    public void setLastModify(String lastModify) {
        this.lastModify = lastModify;
    }

    public int getReqID() {
        return reqID;
    }

    public void setReqID(int reqID) {
        this.reqID = reqID;
    }

    public Handler getUpdateHandler() {
        return updateHandler;
    }

    public void setUpdateHandler(Handler updateHandler) {
        this.updateHandler = updateHandler;
    }

    public boolean isNeedVerify() {
        return needVerify;
    }

    public void setNeedVerify(boolean needVerify) {
        this.needVerify = needVerify;
    }



    @Override
    public RequestListBean clone(){
        DownloadRequestBean o = null;
        o = (DownloadRequestBean) super.clone();
        return o;
    }


}
