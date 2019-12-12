LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS:= -fvisibility=hidden -DRUBY_EXPORT  -pthread

# fixed flags
LOCAL_CFLAGS+= -ffunction-sections -fdata-sections

LOCAL_C_INCLUDES:= \
        $(LOCAL_PATH)\
		$(LOCAL_PATH)/ext/zlib\
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
        dln.c\
        encoding.c\
        version.c\
        array.c\
        bignum.c\
        class.c\
        compar.c\
        complex.c\
        dir.c\
        dln_find.c\
        enum.c\
        enumerator.c\
        error.c\
        eval.c\
        load.c\
        proc.c\
        file.c\
        gc.c\
        hash.c\
        inits.c\
        io.c\
        marshal.c\
        math.c\
        node.c\
        numeric.c\
        object.c\
        pack.c\
        parse.c\
        process.c\
        random.c\
        range.c\
        rational.c\
        re.c\
        regcomp.c\
        regenc.c\
        regerror.c\
        regexec.c\
        regparse.c\
        regsyntax.c\
        ruby.c\
        safe.c\
        signal.c\
        sprintf.c\
        st.c\
        strftime.c\
        string.c\
        struct.c\
        time.c\
        transcode.c\
        util.c\
        variable.c\
        compile.c\
        debug.c\
        iseq.c\
        vm.c\
        vm_dump.c\
        thread.c\
        cont.c\
        enc/ascii.c\
        enc/us_ascii.c\
        enc/unicode.c\
        enc/utf_8.c\
		enc/shift_jis.c\
		enc/trans/japanese_sjis.c\
        newline.c\
        missing/memcmp.c\
        missing/crypt.c\
        missing/isinf.c\
		missing/strlcat.c\
		missing/strlcpy.c\
		missing/langinfo.c\
        miniprelude.c\
        dmyext.c\
		ext/zlib/zlib.c
LOCAL_MODULE := ruby

include $(BUILD_STATIC_LIBRARY)
