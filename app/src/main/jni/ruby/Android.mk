LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS:= -fvisibility=hidden -DRUBY_EXPORT  -pthread

# fixed flags
LOCAL_CFLAGS+= -ffunction-sections -fdata-sections

LOCAL_C_INCLUDES:= \
        $(LOCAL_PATH) \
        $(LOCAL_PATH)/ext/zlib \
        $(LOCAL_PATH)/include
		
ifeq ($(TARGET_ARCH_ABI), armeabi)
	LOCAL_CFLAGS += -DARCH_32BIT
	LOCAL_C_INCLUDES += $(LOCAL_PATH)/config/arm
else ifeq ($(TARGET_ARCH_ABI), armeabi-v7a)
	LOCAL_CFLAGS += -DARCH_32BIT
	LOCAL_C_INCLUDES += $(LOCAL_PATH)/config/arm
else ifeq ($(TARGET_ARCH_ABI), arm64-v8a)
	LOCAL_CFLAGS += -DARCH_32BIT
	LOCAL_C_INCLUDES += $(LOCAL_PATH)/config/arm64
endif
		
LOCAL_SRC_FILES:= \
        $(LOCAL_PATH)/dln.c \
        $(LOCAL_PATH)/encoding.c \
        $(LOCAL_PATH)/version.c \
        $(LOCAL_PATH)/array.c \
        $(LOCAL_PATH)/bignum.c \
        $(LOCAL_PATH)/class.c \
        $(LOCAL_PATH)/compar.c \
        $(LOCAL_PATH)/complex.c \
        $(LOCAL_PATH)/dir.c \
        $(LOCAL_PATH)/dln_find.c \
        $(LOCAL_PATH)/enum.c \
        $(LOCAL_PATH)/enumerator.c \
        $(LOCAL_PATH)/error.c \
        $(LOCAL_PATH)/eval.c \
        $(LOCAL_PATH)/load.c \
        $(LOCAL_PATH)/proc.c \
        $(LOCAL_PATH)/file.c \
        $(LOCAL_PATH)/gc.c \
        $(LOCAL_PATH)/hash.c \
        $(LOCAL_PATH)/inits.c \
        $(LOCAL_PATH)/io.c \
        $(LOCAL_PATH)/marshal.c \
        $(LOCAL_PATH)/math.c \
        $(LOCAL_PATH)/node.c \
        $(LOCAL_PATH)/numeric.c \
        $(LOCAL_PATH)/object.c \
        $(LOCAL_PATH)/pack.c \
        $(LOCAL_PATH)/parse.c \
        $(LOCAL_PATH)/process.c \
        $(LOCAL_PATH)/random.c \
        $(LOCAL_PATH)/range.c \
        $(LOCAL_PATH)/rational.c \
        $(LOCAL_PATH)/re.c \
        $(LOCAL_PATH)/regcomp.c \
        $(LOCAL_PATH)/regenc.c \
        $(LOCAL_PATH)/regerror.c \
        $(LOCAL_PATH)/regexec.c \
        $(LOCAL_PATH)/regparse.c \
        $(LOCAL_PATH)/regsyntax.c \
        $(LOCAL_PATH)/ruby.c \
        $(LOCAL_PATH)/safe.c \
        $(LOCAL_PATH)/signal.c \
        $(LOCAL_PATH)/sprintf.c \
        $(LOCAL_PATH)/st.c \
        $(LOCAL_PATH)/strftime.c \
        $(LOCAL_PATH)/string.c \
        $(LOCAL_PATH)/struct.c \
        $(LOCAL_PATH)/time.c \
        $(LOCAL_PATH)/transcode.c \
        $(LOCAL_PATH)/util.c \
        $(LOCAL_PATH)/variable.c \
        $(LOCAL_PATH)/compile.c \
        $(LOCAL_PATH)/debug.c \
        $(LOCAL_PATH)/iseq.c \
        $(LOCAL_PATH)/vm.c \
        $(LOCAL_PATH)/vm_dump.c \
        $(LOCAL_PATH)/thread.c \
        $(LOCAL_PATH)/cont.c \
        $(LOCAL_PATH)/enc/ascii.c \
        $(LOCAL_PATH)/enc/us_ascii.c \
        $(LOCAL_PATH)/enc/unicode.c \
        $(LOCAL_PATH)/enc/utf_8.c \
        $(LOCAL_PATH)/enc/shift_jis.c \
        $(LOCAL_PATH)/enc/trans/japanese_sjis.c \
        $(LOCAL_PATH)/newline.c \
        $(LOCAL_PATH)/missing/memcmp.c \
        $(LOCAL_PATH)/missing/crypt.c \
        $(LOCAL_PATH)/missing/isinf.c \
        $(LOCAL_PATH)/missing/strlcat.c \
        $(LOCAL_PATH)/missing/strlcpy.c \
        $(LOCAL_PATH)/missing/langinfo.c \
        $(LOCAL_PATH)/miniprelude.c \
        $(LOCAL_PATH)/dmyext.c \
        $(LOCAL_PATH)/ext/zlib/zlib.c
LOCAL_MODULE := ruby

include $(BUILD_STATIC_LIBRARY)
