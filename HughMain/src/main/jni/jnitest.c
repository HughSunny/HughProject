//
// Created by HughShi on 2017/5/25.
//

#include "com_set_util_JniUtils.h"
#include "android/log.h"
#define  LOG_TAG    "【C_LOG】"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

JNIEXPORT jstring JNICALL Java_com_set_util_JniUtils_getStringFormC
        (JNIEnv *env, jobject thiz){
    LOGI("调用 C getStringFormC() 方法\n");
    return (*env)->NewStringUTF(env,"这里是来自c的string");}

