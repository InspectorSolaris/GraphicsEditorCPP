//
// Created by herman on 23.05.19.
//

#ifndef GRAPHICSEDITORCPP_ASTAR_H
#define GRAPHICSEDITORCPP_ASTAR_H

#include <jni.h>
#include <android/bitmap.h>

#include <algorithm>
#include <vector>
#include <queue>
#include <cmath>

extern "C" JNIEXPORT jintArray JNICALL
Java_com_example_graphicseditorcpp_AStarActivity_algorithmAStar(
        JNIEnv *env,
        jobject,
        jobject bitmap,
        jint start_x,
        jint start_y,
        jint finish_x,
        jint finish_y,
        jint empirics,
        jint directions,
        jint pixel_size);

#endif //GRAPHICSEDITORCPP_ASTAR_H
