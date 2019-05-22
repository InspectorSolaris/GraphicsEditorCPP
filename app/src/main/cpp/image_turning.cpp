//
// Created by herman on 23.05.19.
//

#include "image_turning.h"

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_TurningActivity_imageTurning(
        JNIEnv *env,
        jobject,
        jdouble angle,
        jobject img_orig,
        jobject img_turn)
{
    AndroidBitmapInfo  orig_info;
    AndroidBitmapInfo  turn_info;
    void             * orig_ptr = nullptr;
    void             * turn_ptr = nullptr;

    AndroidBitmap_getInfo(env, img_orig, &orig_info);
    AndroidBitmap_getInfo(env, img_turn, &turn_info);

    AndroidBitmap_lockPixels(env, img_orig, &orig_ptr);
    AndroidBitmap_lockPixels(env, img_turn, &turn_ptr);

    long double pi = 3.1415926535897931L;
    long double a = angle * pi / 180.0L;
    long double n = turn_info.height;
    long double m = turn_info.width;

    auto orig = (uint32_t *)orig_ptr;
    auto turn = (uint32_t *)turn_ptr;
    for(unsigned int i = 0; i < n; ++i)
    {
        for(unsigned int j = 0; j < m; ++j)
        {
            int x_position = (int)(j - m / 2);
            int y_position = (int)(n / 2 - i);

            long double x_turn = x_position * cos(a) - y_position * sin(a);
            long double y_turn = y_position * cos(a) + x_position * sin(a);

            int x = (int)(x_turn + m / 2);
            int y = (int)(n / 2 - y_turn);

            if (0 <= x && x < m &&
                0 <= y && y < n)
            {
                turn[i * (int) m + j] = orig[y * (int) m + x];
            }
            else
            {
                turn[i * (int)m + j] = 0x00000000;
            }
        }
    }

    AndroidBitmap_unlockPixels(env, img_orig);
    AndroidBitmap_unlockPixels(env, img_turn);
}