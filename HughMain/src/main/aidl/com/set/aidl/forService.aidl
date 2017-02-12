// forService.aidl
package com.set.aidl;

// Declare any non-default types here with import statements
import com.set.aidl.forActivity;
import com.set.aidl.data.TransDataBean;
interface forService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    void registerTestCall(forActivity cb);
    void invokCallBack();
    TransDataBean getCount();
}
