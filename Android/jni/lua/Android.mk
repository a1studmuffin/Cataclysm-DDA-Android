LOCAL_PATH := $(call my-dir)

###########################
#
# Lua shared library
#
###########################

include $(CLEAR_VARS)

LOCAL_MODULE := lua

LOCAL_C_INCLUDES := $(LOCAL_PATH)/src

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_C_INCLUDES)

LOCAL_SRC_FILES := src/lapi.c \
	src/lauxlib.c \
	src/lbaselib.c \
	src/lcode.c \
	src/ldblib.c \
	src/ldebug.c \
	src/ldo.c \
	src/ldump.c \
	src/lfunc.c \
	src/lgc.c \
	src/linit.c \
	src/liolib.c \
	src/llex.c \
	src/lmathlib.c \
	src/lmem.c \
	src/loadlib.c \
	src/lobject.c \
	src/lopcodes.c \
	src/loslib.c \
	src/lparser.c \
	src/lstate.c \
	src/lstring.c \
	src/lstrlib.c \
	src/ltable.c \
	src/ltablib.c \
	src/ltm.c \
	src/lundump.c \
	src/lvm.c \
	src/lzio.c \
	src/print.c

include $(BUILD_SHARED_LIBRARY)
