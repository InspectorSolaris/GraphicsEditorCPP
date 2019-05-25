//
// Created by herman on 23.05.19.
//

#ifndef GRAPHICSEDITORCPP_IMAGE_COLORCORRECTION_H
#define GRAPHICSEDITORCPP_IMAGE_COLORCORRECTION_H

#include <jni.h>
#include <android/bitmap.h>

#include <algorithm>

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_FiltersActivity_imageColorcorrection(
        JNIEnv *env,
        jobject,
        jobject img_orig,
        jobject img_clrd,
        jint filter);

#endif //GRAPHICSEDITORCPP_IMAGE_COLORCORRECTION_H
