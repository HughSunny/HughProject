package set.work.bean;

/**
 * Created by Hugh on 2016/8/31.
 */
public class DownloadRequestBean extends RequestListBean implements Cloneable {

    private String filePath;
    private String fileName;
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

    @Override
    public RequestListBean clone(){
        DownloadRequestBean o = null;
        o = (DownloadRequestBean) super.clone();
        return o;
    }


}
