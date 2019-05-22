//
// Created by herman on 23.05.19.
//

#ifndef GRAPHICSEDITORCPP_IMAGE_TURNING_H
#define GRAPHICSEDITORCPP_IMAGE_TURNING_H

#include <jni.h>
#include <android/bitmap.h>

#include <cmath>

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_TurningActivity_imageTurning(
        JNIEnv *env,
        jobject,
        jdouble angle,
        jobject img_orig,
        jobject img_turn);

#endif //GRAPHICSEDITORCPP_IMAGE_TURNING_H
