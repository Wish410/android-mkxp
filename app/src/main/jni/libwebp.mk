LOCAL_PATH:= $(call my-dir)/libwebp

include $(CLEAR_VARS)
LOCAL_SRC_FILES := \
    $(LOCAL_PATH)/src/dec/alpha.c \
    $(LOCAL_PATH)/src/dec/buffer.c \
    $(LOCAL_PATH)/src/dec/frame.c \
    $(LOCAL_PATH)/src/dec/idec.c \
    $(LOCAL_PATH)/src/dec/io.c \
    $(LOCAL_PATH)/src/dec/layer.c \
    $(LOCAL_PATH)/src/dec/quant.c \
    $(LOCAL_PATH)/src/dec/tree.c \
    $(LOCAL_PATH)/src/dec/vp8.c \
    $(LOCAL_PATH)/src/dec/vp8l.c \
    $(LOCAL_PATH)/src/dec/webp.c \
    $(LOCAL_PATH)/src/dsp/cpu.c \
    $(LOCAL_PATH)/src/dsp/dec.c \
    $(LOCAL_PATH)/src/dsp/dec_sse2.c \
    $(LOCAL_PATH)/src/dsp/enc.c \
    $(LOCAL_PATH)/src/dsp/enc_sse2.c \
    $(LOCAL_PATH)/src/dsp/lossless.c \
    $(LOCAL_PATH)/src/dsp/upsampling.c \
    $(LOCAL_PATH)/src/dsp/upsampling_sse2.c \
    $(LOCAL_PATH)/src/dsp/yuv.c \
    $(LOCAL_PATH)/src/enc/alpha.c \
    $(LOCAL_PATH)/src/enc/analysis.c \
    $(LOCAL_PATH)/src/enc/backward_references.c \
    $(LOCAL_PATH)/src/enc/config.c \
    $(LOCAL_PATH)/src/enc/cost.c \
    $(LOCAL_PATH)/src/enc/filter.c \
    $(LOCAL_PATH)/src/enc/frame.c \
    $(LOCAL_PATH)/src/enc/histogram.c \
    $(LOCAL_PATH)/src/enc/iterator.c \
    $(LOCAL_PATH)/src/enc/layer.c \
    $(LOCAL_PATH)/src/enc/picture.c \
    $(LOCAL_PATH)/src/enc/quant.c \
    $(LOCAL_PATH)/src/enc/syntax.c \
    $(LOCAL_PATH)/src/enc/token.c \
    $(LOCAL_PATH)/src/enc/tree.c \
    $(LOCAL_PATH)/src/enc/vp8l.c \
    $(LOCAL_PATH)/src/enc/webpenc.c \
    $(LOCAL_PATH)/src/utils/bit_reader.c \
    $(LOCAL_PATH)/src/utils/bit_writer.c \
    $(LOCAL_PATH)/src/utils/color_cache.c \
    $(LOCAL_PATH)/src/utils/filters.c \
    $(LOCAL_PATH)/src/utils/huffman.c \
    $(LOCAL_PATH)/src/utils/huffman_encode.c \
    $(LOCAL_PATH)/src/utils/quant_levels.c \
    $(LOCAL_PATH)/src/utils/quant_levels_dec.c \
    $(LOCAL_PATH)/src/utils/rescaler.c \
    $(LOCAL_PATH)/src/utils/thread.c \
    $(LOCAL_PATH)/src/utils/utils.c \

LOCAL_CFLAGS := -Wall -DANDROID -DHAVE_MALLOC_H -DHAVE_PTHREAD \
                -DWEBP_USE_THREAD \
                -finline-functions -frename-registers -ffast-math \
                -s -fomit-frame-pointer -Isrc/webp

LOCAL_C_INCLUDES += $(LOCAL_PATH)/src

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
  # Setting LOCAL_ARM_NEON will enable -mfpu=neon which may cause illegal
  # instructions to be generated for armv7a code. Instead target the neon code
  # specifically.
  LOCAL_SRC_FILES += $(LOCAL_PATH)/src/dsp/dec_neon.c.neon
  LOCAL_SRC_FILES += $(LOCAL_PATH)/src/dsp/upsampling_neon.c.neon
  LOCAL_SRC_FILES += $(LOCAL_PATH)/src/dsp/enc_neon.c.neon
endif
LOCAL_STATIC_LIBRARIES := cpufeatures

LOCAL_MODULE:= webp

include $(BUILD_STATIC_LIBRARY)

$(call import-module,android/cpufeatures)
