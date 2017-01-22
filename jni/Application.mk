
# Uncomment this if you're using STL in your project
# See CPLUSPLUS-SUPPORT.html in the NDK documentation for more information
APP_STL := gnustl_static
APP_CPPFLAGS += -std=c++11
#APP_ABI := armeabi armeabi-v7a x86
APP_ABI := armeabi
#LOCAL_C_INCLUDES += ${ANDROID_NDK}/sources/cxx-stl/gnu-libstdc++/{NDK_TOOLCHAIN_VERSION}/include
# Min SDK level
APP_PLATFORM=android-10

