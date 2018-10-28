//
// Created by HughShi on 2017/5/31.
//

#include <jni.h>
#include "libavcodec/avcodec.h"

JNIEXPORT jstring JNICALL Java_com_set_util_FFmpegUtil_getInfo
        (JNIEnv * env, jobject thiz){
    char info[10000] = { 0 };
    sprintf(info, "%s\n", avcodec_configuration());
    return (*env)->NewStringUTF(env, info);
}