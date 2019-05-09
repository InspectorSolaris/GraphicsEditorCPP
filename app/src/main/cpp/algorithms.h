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

#include <cmath>
#include <vector>
#include <queue>
#include <tuple>
#include <climits>

extern "C" JNIEXPORT void JNICALL
pictureTurning();

// parameters: original pic, ...
// return: color corrected picture

extern "C" JNIEXPORT void JNICALL
pictureColorcorrection(); // Dasha

// parameters: original pic, scale ratio
// return: scaled on specified ratio picture

extern "C" JNIEXPORT void JNICALL
pictureScaling();

// parameters: original pic, ...
// return: ...

extern "C" JNIEXPORT void JNICALL
pictureSegmentation();

// parameters: original pic, retouching parameters
// return: retouched pic

extern "C" JNIEXPORT void JNICALL
pictureRetouching();

// parameters: original pic, retouching parameters
// return: masked pic

extern "C" JNIEXPORT void JNICALL
pictureUnsharpMasking();

// parameters: original pic, bilinear algorithm parameters
// return: processed pic

extern "C" JNIEXPORT void JNICALL
pictureBilinearFiltration();

// parameters: original pic, trilinear algorithm parameters
// return: processed pic

extern "C" JNIEXPORT void JNICALL
pictureTrilinearFiltration();

// parameters: original pic (clear pic), positions of points
// return: interpolated with splines broken line on original pic

extern "C" JNIEXPORT void JNICALL
drawLines(); // Asya

// parameters: map, start position, finish position
// return: shortest way by A-Star

extern "C" JNIEXPORT jintArray JNICALL
algorithmAStar(
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
        jint map_y); // Herman

#endif //GRAPHICSEDITORCPP_ALGORITHMS_H
