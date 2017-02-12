package com.hugh.work.listener;

import android.app.ProgressDialog;

public interface ProgressLis {
	ProgressDialog onShowProgress();
	ProgressDialog onShowWarningPgs(String errorMsg);
}
