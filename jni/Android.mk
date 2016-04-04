LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := app
LOCAL_SRC_FILES := \
	/Users/Justin/Documents/FYP_HKUST/app/src/main/jniLibs/armeabi-v7a/libcaffe.so

include $(PREBUILT_SHARED_LIBRARY)
