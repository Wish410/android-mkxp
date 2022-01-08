APP_ABI := armeabi-v7a arm64-v8a x86_64
APP_PLATFORM := android-21
APP_OPTIM := release
APP_CPPFLAGS := -std=c++11 -frtti -fexceptions -Os -fPIC
APP_STL := c++_shared

ifeq ($(TARGET_ARCH_ABI), armeabi)
	APP_CPPFLAGS += -DARCH_32BIT -DABI_ARMEABI_V7A
else ifeq ($(TARGET_ARCH_ABI), armeabi-v7a)
	APP_CPPFLAGS += -DARCH_32BIT -DABI_ARMEABI_V7A
else ifeq ($(TARGET_ARCH_ABI), arm64-v8a)
	APP_CPPFLAGS += -DARCH_64BIT -DABI_ARM64_V8A
else ifeq ($(TARGET_ARCH_ABI), x86)
	APP_CPPFLAGS += -DARCH_32BIT -DABI_X86
else ifeq ($(TARGET_ARCH_ABI), x86_64)
	APP_CPPFLAGS += -DARCH_64BIT -DABI_X86_64
endif
