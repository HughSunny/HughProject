package set.work.comm.expt;

import set.work.comm.ICommonResultCode;

/**
 * Created by Hugh on 2017/2/4.
 */

public class ParameterException extends RuntimeException {

    private String notice;
    public ParameterException(ICommonResultCode mCode){
        super(mCode.toString());
    }


    public ParameterException(ICommonResultCode mCode,String mNotice){
        super(mCode.toString() + "" + mNotice);
        notice = mNotice;
    }

}
