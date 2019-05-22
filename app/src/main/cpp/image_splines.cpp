//
// Created by herman on 23.05.19.
//

#include "image_splines.h"

void drawCircle(
        int x,
        int y,
        int r,
        uint32_t c,
        uint32_t * img_ptr,
        AndroidBitmapInfo info)
{
    for(int i = -r / 2; i <= r / 2; ++i)
    {
        for(int j = -r / 2; j <= r / 2; ++j)
        {
            int px = std::max(std::min(x + i, (int)info.width - 1), 0);
            int py = std::max(std::min(y + j, (int)info.height - 1), 0);

            if ((px - x) * (px - x) + (py - y) * (py - y) < r)
            {
                img_ptr[py * info.width + px] = c;
            }
        }
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_SplinesActivity_drawCircle(
        JNIEnv *env,
        jobject,
        jint x,
        jint y,
        jint r,
        jint c,
        jobject img)
{
    AndroidBitmapInfo  info;
    void             * ptr;

    AndroidBitmap_getInfo(env, img, &info);
    AndroidBitmap_lockPixels(env, img, &ptr);

    drawCircle(x, y, r, (uint32_t)c, (uint32_t *)ptr, info);

    AndroidBitmap_unlockPixels(env, img);
}

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
        jobject img)
{
    jint * px = env->GetIntArrayElements(px_coords, nullptr);
    jint * py = env->GetIntArrayElements(py_coords, nullptr);

    jdouble * p1x = env->GetDoubleArrayElements(p1x_coords, nullptr);
    jdouble * p1y = env->GetDoubleArrayElements(p1y_coords, nullptr);
    jdouble * p2x = env->GetDoubleArrayElements(p2x_coords, nullptr);
    jdouble * p2y = env->GetDoubleArrayElements(p2y_coords, nullptr);

    AndroidBitmapInfo  info;
    void             * ptr;

    AndroidBitmap_getInfo(env, img, &info);
    AndroidBitmap_lockPixels(env, img, &ptr);

    int splineLen = 250;
    auto img_ptr = (uint32_t *)ptr;
    for(int i = 0; i < n - 1; ++i)
    {
        for(int j = 0; j < splineLen; ++j)
        {
            double t = (double)j / (double)splineLen;

            double ca = (1 - t) * (1 - t) * (1 - t);
            double cb = (1 - t) * (1 - t) * t;
            double cc = (1 - t) * t * t;
            double cd = t * t * t;

            double x = (ca * px[i] + cb * 3 * p1x[i] + cc * 3 * p2x[i] + cd * px[i + 1]);
            double y = (ca * py[i] + cb * 3 * p1y[i] + cc * 3 * p2y[i] + cd * py[i + 1]);

            drawCircle((int)x, (int)y, r, (uint32_t)c, img_ptr, info);
        }
    }

    AndroidBitmap_unlockPixels(env, img);
}

extern "C" JNIEXPORT jdoubleArray JNICALL
Java_com_example_graphicseditorcpp_SplinesActivity_calculateSplinesP1(
        JNIEnv *env,
        jobject,
        jint n,
        jintArray coords)
{
    using namespace std;

    jint * crds = env->GetIntArrayElements(coords, nullptr);
    vector<double> p1((unsigned int)n - 1);
    vector<double> p2((unsigned int)n - 1);
    vector<double> d((unsigned int)n - 1);

    --n;
    vector<double> a((unsigned int)n);
    vector<double> b((unsigned int)n);
    vector<double> c((unsigned int)n);

    a[0] = 0;
    b[0] = 2;
    c[0] = 1;
    d[0] = crds[0] + 2 * crds[1];

    for(unsigned int i = 1; i < n - 1; ++i)
    {
        a[i] = 1;
        b[i] = 4;
        c[i] = 1;
        d[i] = 4 * crds[i] + 2 * crds[i + 1];
    }

    a[n - 1] = 2;
    b[n - 1] = 7;
    c[n - 1] = 0;
    d[n - 1] = 8 * crds[n - 1] + crds[n];

    for(unsigned int i = 1; i < n; ++i)
    {
        double w = a[i] / b[i - 1];
        b[i] = b[i] - w * c[i - 1];
        d[i] = d[i] - w * d[i - 1];
    }

    p1[n - 1] = d[n - 1] / b[n - 1];
    for(unsigned int i = 0; i < n - 1; ++i)
    {
        p1[n - 2 - i] = (d[n - 2 - i] - c[n - 2 - i] * p1[n - 2 - i + 1]) / b[n - 2 - i];
    }

    for(unsigned int i = 0; i < n - 1; ++i)
    {
        p2[i] = 2 * crds[i + 1] - p1[i + 1];
    }

    jdoubleArray result = env->NewDoubleArray(n);
    jdouble buf[n];

    for(unsigned int i = 0; i < n; ++i)
    {
        buf[i] = p1[i];
    }

    env->SetDoubleArrayRegion(result, 0, n, buf);

    return result;
}

extern "C" JNIEXPORT jdoubleArray JNICALL
Java_com_example_graphicseditorcpp_SplinesActivity_calculateSplinesP2(
        JNIEnv *env,
        jobject,
        jint n,
        jintArray coords)
{
    using namespace std;

    jint * crds = env->GetIntArrayElements(coords, nullptr);
    vector<double> p1((unsigned int)n - 1);
    vector<double> p2((unsigned int)n - 1);
    vector<double> d((unsigned int)n - 1);

    --n;
    vector<double> a((unsigned int)n);
    vector<double> b((unsigned int)n);
    vector<double> c((unsigned int)n);

    a[0] = 0;
    b[0] = 2;
    c[0] = 1;
    d[0] = crds[0] + 2 * crds[1];

    for(unsigned int i = 1; i < n - 1; ++i)
    {
        a[i] = 1;
        b[i] = 4;
        c[i] = 1;
        d[i] = 4 * crds[i] + 2 * crds[i + 1];
    }

    a[n - 1] = 2;
    b[n - 1] = 7;
    c[n - 1] = 0;
    d[n - 1] = 8 * crds[n - 1] + crds[n];

    for(unsigned int i = 1; i < n; ++i)
    {
        double w = a[i] / b[i - 1];
        b[i] = b[i] - w * c[i - 1];
        d[i] = d[i] - w * d[i - 1];
    }

    p1[n - 1] = d[n - 1] / b[n - 1];
    for(unsigned int i = 0; i < n - 1; ++i)
    {
        p1[n - 2 - i] = (d[n - 2 - i] - c[n - 2 - i] * p1[n - 2 - i + 1]) / b[n - 2 - i];
    }

    for(unsigned int i = 0; i < n - 1; ++i)
    {
        p2[i] = 2 * crds[i + 1] - p1[i + 1];
    }
    p2[n - 1] = 0.5 * (crds[n] + p1[n - 1]);

    jdoubleArray result = env->NewDoubleArray(n);
    jdouble buf[n];

    for(unsigned int i = 0; i < n; ++i)
    {
        buf[i] = p2[i];
    }

    env->SetDoubleArrayRegion(result, 0, n, buf);

    return result;
}