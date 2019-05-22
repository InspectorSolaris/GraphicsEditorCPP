//
// Created by herman on 23.05.19.
//

#ifndef GRAPHICSEDITORCPP_IMAGE_SPLINES_H
#define GRAPHICSEDITORCPP_IMAGE_SPLINES_H

#include <jni.h>
#include <android/bitmap.h>

#include <algorithm>
#include <vector>

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_SplinesActivity_drawCircle(
        JNIEnv *env,
        jobject,
        jint x,
        jint y,
        jint r,
        jint c,
        jobject img);

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_SplinesActivity_drawSplines(
        JNIEnv *env,
        jobject,
        jint n,
        jint r,
        jint c,
        jintArray px_coords,
        jintArray py_coords,
        jdoubleArray p1x_coords,
        jdoubleArray p1y_coords,
        jdoubleArray p2x_coords,
        jdoubleArray p2y_coords,
        jobject img);

extern "C" JNIEXPORT jdoubleArray JNICALL
Java_com_example_graphicseditorcpp_SplinesActivity_calculateSplinesP1(
        JNIEnv *env,
        jobject,
        jint n,
        jintArray coords);

extern "C" JNIEXPORT jdoubleArray JNICALL
Java_com_example_graphicseditorcpp_SplinesActivity_calculateSplinesP2(
        JNIEnv *env,
        jobject,
        jint n,
        jintArray coords);


#endif //GRAPHICSEDITORCPP_IMAGE_SPLINES_H
