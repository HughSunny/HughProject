package set.work.comm;

/**
 * Created by Hugh on 2017/2/4.
 */

public enum ICommonResultCode {

    PARAMETER_IS_NULL("e001","参数为空"),
    INVALID_PARAMETER("e002","非法参数");

    private String value;
    private String desc;

    private ICommonResultCode(String value, String desc) {
        this.setValue(value);
        this.setDesc(desc);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "[" + this.value + "]" + this.desc;
    }

}
