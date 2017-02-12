package com.set.aidl.data;

import android.os.Parcel;
import android.os.Parcelable;

public class TransDataBean implements Parcelable {

	public static final Parcelable.Creator<TransDataBean> CREATOR = new Creator<TransDataBean>(){

		@Override
		public TransDataBean createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			TransDataBean  transData = new TransDataBean();
			transData.count = source.readInt();
			return transData;
		}

		@Override
		public TransDataBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new TransDataBean[size];
		}
		
	};
	
    public int count;
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(this.count);
	}

}
