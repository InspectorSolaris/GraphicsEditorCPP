//
// Created by herman on 23.05.19.
//

#include "image_retouching.h"

uint32_t blurPixel(
        int x,
        int y,
        int n,
        int m,
        int r,
        const uint32_t * img)
{
    uint32_t result = 0xFF000000;

    if(0 <= x && x < m &&
       0 <= y && y < n)
    {
        result &= img[x * m + y];
    }

    uint32_t rc = 0;
    uint32_t gc = 0;
    uint32_t bc = 0;
    int counter = 0;

    for(int i = -r; i <= r; ++i)
    {
        for(int j = -r; j <= r; ++j)
        {
            if(0 <= x + j && x + j < m &&
               0 <= y + i && y + i < n)
            {
                rc += (img[(y + i) * m + (x + j)] & 0x00FF0000U) >> 16U;
                gc += (img[(y + i) * m + (x + j)] & 0x0000FF00U) >> 8U;
                bc += (img[(y + i) * m + (x + j)] & 0x000000FFU) >> 0U;
                ++counter;
            }
        }
    }

    rc /= counter;
    gc /= counter;
    bc /= counter;

    result += rc << 16U;
    result += gc << 8U;
    result += bc << 0U;

    return result;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_RetouchingActivity_imageRetouching(
        JNIEnv *env,
        jobject,
        jint x,
        jint y,
        jint r,
        jobject img)
{
    AndroidBitmapInfo  info;
    void             * ptr = nullptr;

    AndroidBitmap_getInfo(env, img, &info);
    AndroidBitmap_lockPixels(env, img, &ptr);

    using namespace std;

    vector<uint32_t> new_rgb;

    int n = info.height;
    int m = info.width;
    auto img_ptr = (uint32_t *)ptr;
    for(int i = max(y - r, 0); i < min(y + r, n); ++i)
    {
        for(int j = max(x - r, 0); j < min(x + r, m); ++j)
        {
            if((i - y) * (i - y) + (j - x) * (j - x) <= r * r)
            {
                new_rgb.push_back(blurPixel(j, i, n, m, (int)ceil(log((r + 1) - (int)sqrt((i - y) * (i - y) + (j - x) * (j - x)))), img_ptr));
            }
        }
    }

    int k = 0;
    for(int i = max(y - r, 0); i < min(y + r, n); ++i)
    {
        for(int j = max(x - r, 0); j < min(x + r, m); ++j)
        {
            if((i - y) * (i - y) + (j - x) * (j - x) <= r * r)
            {
                img_ptr[i * m + j] = new_rgb[k++];
            }
        }
    }

    AndroidBitmap_unlockPixels(env, img);
}