//
// Created by herman on 23.05.19.
//

#include "image_colorcorrection.h"

uint32_t colorcorrectionBlurPixel(
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
        result &= img[y * m + x];
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

uint32_t newPixel(
        int filter,
        uint32_t a,
        uint32_t r,
        uint32_t g,
        uint32_t b,
        int x,
        int y,
        int n,
        int m,
        uint32_t * img)
{
    if(filter == 1) //bnw
    {
        uint32_t color = (r + g + b) / 3;

        r = color;
        g = color;
        b = color;
    }
    else if(filter == 2) //negative
    {
        r = 255 - r;
        g = 255 - g;
        b = 255 - b;
    }
    else if(filter == 3) //shouldbesepia
    {
        auto new_r = (0.393 * r + 0.769 * g + 0.189 * b);
        auto new_g = (0.349 * r + 0.686 * g + 0.168 * b);
        auto new_b = (0.272 * r + 0.534 * g + 0.131 * b);

        if (new_r >255) r = 255;
        else r = new_r;
        if (new_g >255) r = 255;
        else g = new_g;
        if (new_b >255) r = 255;
        else b = new_b;



    }
    else if(filter == 4) //bluer
    {
        uint32_t pixel = colorcorrectionBlurPixel(x, y, n, m, 10, img);

        r = (pixel & 0x00FF0000U) >> 16U;
        g = (pixel & 0x0000FF00U) >> 8U;
        b = (pixel & 0x000000FFU) >> 0U;
    }
    else if (filter==5) //brightness
    {
        r+=50;
        g+=50;
        b+=50;
        if (r>255) r=255;
        if (g>255) g=255;
        if (b>255) b=255;
    }
    else if (filter==6) //opacity
    {
        if (r>245 && g>245 && b>245) a=0;
    }
    else if (filter==7) //red
    {
        g=0;
        b=0;
    }
    else if (filter==8) //green
    {
        r=0;
        b=0;
    }
    else if (filter==9) //blue
    {
        r=0;
        g=0;
    }

    return (a << 24U) | (r << 16U) | (g << 8U) | (b << 0U);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_FiltersActivity_imageColorcorrection(
        JNIEnv *env,
        jobject,
        jobject img_orig,
        jobject img_clrd,
        jint filter)
{
    AndroidBitmapInfo  orig_info;
    AndroidBitmapInfo  clrd_info;
    void             * orig_ptr = nullptr;
    void             * clrd_ptr = nullptr;

    AndroidBitmap_getInfo(env, img_orig, &orig_info);
    AndroidBitmap_getInfo(env, img_clrd, &clrd_info);

    AndroidBitmap_lockPixels(env, img_orig, &orig_ptr);
    AndroidBitmap_lockPixels(env, img_clrd, &clrd_ptr);

    int n = orig_info.height;
    int m = orig_info.width;
    auto orig = (uint32_t *)orig_ptr;
    auto clrd = (uint32_t *)clrd_ptr;
    for(int i = 0; i < n; ++i)
    {
        for(int j = 0; j < m; ++j)
        {
            uint32_t a = (orig[i * m + j] & 0xFF000000U) >> 24U;
            uint32_t r = (orig[i * m + j] & 0x00FF0000U) >> 16U;
            uint32_t g = (orig[i * m + j] & 0x0000FF00U) >> 8U;
            uint32_t b = (orig[i * m + j] & 0x000000FFU) >> 0U;

            clrd[i * m + j] = newPixel(filter, a, r, g, b, j, i, n, m, orig);
        }
    }

    AndroidBitmap_unlockPixels(env, img_orig);
    AndroidBitmap_unlockPixels(env, img_clrd);
}