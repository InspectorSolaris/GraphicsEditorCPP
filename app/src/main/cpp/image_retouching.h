//
// Created by herman on 23.05.19.
//

#ifndef GRAPHICSEDITORCPP_IMAGE_RETOUCHING_H
#define GRAPHICSEDITORCPP_IMAGE_RETOUCHING_H

#include <jni.h>
#include <android/bitmap.h>

#include <algorithm>
#include <vector>
#include <cmath>

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_RetouchingActivity_imageRetouching(
        JNIEnv *env,
        jobject,
        jint x,
        jint y,
        jint r,
        jobject img);

#endif //GRAPHICSEDITORCPP_IMAGE_RETOUCHING_H
