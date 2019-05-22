//
// Created by herman on 26.04.19.
//

// Interface of algorithms used in graphics editor

#ifndef GRAPHICSEDITORCPP_ALGORITHMS_H
#define GRAPHICSEDITORCPP_ALGORITHMS_H

// parameters: original pic, angle of rotation
// return: turned on specified angle picture

#include <jni.h>
#include <android/bitmap.h>

#include <algorithm>
#include <string>
#include <iostream>
#include <cmath>
#include <vector>
#include <queue>
#include <tuple>
#include <climits>

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_TurningActivity_imageTurning(
        JNIEnv *env,
        jobject,
        jdouble angle,
        jobject image_orig,
        jobject image_turn);

// parameters: original pic, ...
// return: color corrected picture

extern "C" JNIEXPORT void JNICALL
pictureColorcorrection(); // Dasha

// parameters: original pic, scale ratio
// return: scaled on specified ratio picture

extern "C" JNIEXPORT void JNICALL
pictureScaling(); // Asya

// parameters: original pic, ...
// return: ...

extern "C" JNIEXPORT void JNICALL
pictureSegmentation();

// parameters: original pic, retouching parameters
// return: retouched pic

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_RetouchingActivity_imageRetouching(
        JNIEnv *env,
        jobject,
        jint r,
        jint x,
        jint y,
        jobject image);

// parameters: original pic, retouching parameters
// return: masked pic

extern "C" JNIEXPORT void JNICALL
pictureUnsharpMasking();

// parameters: original pic, bilinear algorithm parameters
// return: processed pic

extern "C" JNIEXPORT void JNICALL
pictureBilinearFiltration(); // Herman

// parameters: original pic, trilinear algorithm parameters
// return: processed pic

extern "C" JNIEXPORT void JNICALL
pictureTrilinearFiltration(); // Herman

// parameters: original pic (clear pic), positions of points
// return: interpolated with splines broken line on original pic

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
        jobject obj,
        jint n,
        jintArray coords);

extern "C" JNIEXPORT jdoubleArray JNICALL
Java_com_example_graphicseditorcpp_SplinesActivity_calculateSplinesP2(
        JNIEnv *env,
        jobject obj,
        jint n,
        jintArray coords);

// parameters: map, start position, finish position
// return: shortest way by A-Star

extern "C" JNIEXPORT jintArray JNICALL
Java_com_example_graphicseditorcpp_AStarActivity_algorithmAStar(
        JNIEnv *env,
        jobject obj,
        jobject bitmap,
        jint start_x,
        jint start_y,
        jint finish_x,
        jint finish_y,
        jint empirics,
        jint directions,
        jint pixel_size,
        jint map_x,
        jint map_y);

#endif //GRAPHICSEDITORCPP_ALGORITHMS_H
