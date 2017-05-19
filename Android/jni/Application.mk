# Reference: https://developer.android.com/ndk/guides/application_mk.html

# See CPLUSPLUS-SUPPORT.html in the NDK documentation for more information
APP_STL := c++_shared
APP_CPPFLAGS += -std=c++11
APP_LDFLAGS += -fuse-ld=gold

# armeabi-v7a covers 98.5%, x86 is 1.5%, armeabi is 0%
# See http://hwstats.unity3d.com/mobile/cpu.html
#APP_ABI := armeabi armeabi-v7a x86
APP_ABI := armeabi-v7a

# Do not specify APP_OPTIM here, it is done through ndk-build NDK_DEBUG=0/1 setting instead
# See https://developer.android.com/ndk/guides/ndk-build.html#dvr
#APP_OPTIM := debug
#APP_OPTIM := release

# Min SDK level
APP_PLATFORM=android-15

