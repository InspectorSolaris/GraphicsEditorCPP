//
// Created by herman on 26.04.19.
//

#include "algorithms.h"

// image turning

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_TurningActivity_imageTurning(
        JNIEnv *env,
        jobject,
        jdouble angle,
        jobject image_orig,
        jobject image_turn)
{
    AndroidBitmapInfo  orig_info;
    AndroidBitmapInfo  turn_info;
    void             * orig_ptr = nullptr;
    void             * turn_ptr = nullptr;

    AndroidBitmap_getInfo(env, image_orig, &orig_info);
    AndroidBitmap_getInfo(env, image_turn, &turn_info);

    AndroidBitmap_lockPixels(env, image_orig, &orig_ptr);
    AndroidBitmap_lockPixels(env, image_turn, &turn_ptr);

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

            int x = (int) (x_turn + m / 2);
            int y = (int) (n / 2 - y_turn);

            if (0 <= x && x < m &&
                0 <= y && y < n) {
                turn[i * (int) m + j] = orig[y * (int) m + x];
            }
            else
            {
                turn[i * (int)m + j] = 0x00000000;
            }
        }
    }

    AndroidBitmap_unlockPixels(env, image_orig);
    AndroidBitmap_unlockPixels(env, image_turn);
}

// color correction

extern "C" JNIEXPORT void JNICALL
pictureColorcorrection();

// image scaling

extern "C" JNIEXPORT void JNICALL
pictureScaling();

// segmentation

extern "C" JNIEXPORT void JNICALL
pictureSegmentation();

// retouching

uint32_t blurPixel(
        const int x,
        const int y,
        const int n,
        const int m,
        const int r,
        const uint32_t * img)
{
    uint32_t result = 0xFF000000;

    if(0 <= x && x < n &&
        0 <= y && y < m)
    {
        result &= img[x * m + y];
    }

    int rc = 0;
    int gc = 0;
    int bc = 0;
    int counter = 0;

    for(int i = -r; i <= r; ++i)
    {
        for(int j = -r; j <= r; ++j)
        {
            if(0 <= x + j && x + j < n &&
               0 <= y + i && y + i < m)
            {
                rc += (img[(x + j) * m + (y + i)] & 0x00FF0000) >> 16;
                gc += (img[(x + j) * m + (y + i)] & 0x0000FF00) >> 8;
                bc += (img[(x + j) * m + (y + i)] & 0x000000FF) >> 0;
                ++counter;
            }
        }
    }

    rc /= counter;
    gc /= counter;
    bc /= counter;

    result += rc << 16;
    result += gc << 8;
    result += bc << 0;

    return result;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_graphicseditorcpp_RetouchingActivity_imageRetouching(
        JNIEnv *env,
        jobject,
        jint r,
        jint x,
        jint y,
        jobject image)
{
    AndroidBitmapInfo  info;
    void             * ptr = nullptr;

    AndroidBitmap_getInfo(env, image, &info);
    AndroidBitmap_lockPixels(env, image, &ptr);

    using namespace std;

    vector<uint32_t> new_rgb;

    auto img = (uint32_t *)ptr;
    for(int i = max(y - r, 0); i < min(y + r, (int)info.height); ++i)
    {
        for(int j = max(x - r, 0); j < min(x + r, (int)info.width); ++j)
        {
            if((i - y) * (i - y) + (j - x) * (j - x) <= r * r)
            {
                new_rgb.push_back(blurPixel(i, j, info.height, info.width, (int)ceil(log((r + 1) - (int)sqrt((i - y) * (i - y) + (j - x) * (j - x)))), img));
            }
        }
    }

    int k = 0;
    for(int i = max(y - r, 0); i < min(y + r, (int)info.height); ++i)
    {
        for(int j = max(x - r, 0); j < min(x + r, (int)info.width); ++j)
        {
            if((i - y) * (i - y) + (j - x) * (j - x) <= r * r)
            {
                img[i * info.width + j] = new_rgb[k++];
            }
        }
    }

    AndroidBitmap_unlockPixels(env, image);
}

// unsharp masking

extern "C" JNIEXPORT void JNICALL
pictureUnsharpMasking();

// bilinear filtration

extern "C" JNIEXPORT void JNICALL
pictureBilinearFiltration();

// trilinear filtration

extern "C" JNIEXPORT void JNICALL
pictureTrilinearFiltration();

// splines

extern "C" JNIEXPORT jdoubleArray JNICALL
Java_com_example_graphicseditorcpp_SplinesActivity_calculateSplinesP1(
        JNIEnv *env,
        jobject,
        jint n,
        jintArray coords) {
    // algorithm from https://www.particleincell.com/2012/bezier-splines/

    using namespace std;
    vector<double> points((unsigned int)n);
    vector<double> p1((unsigned int)n - 1);
    vector<double> p2((unsigned int)n - 1);
    vector<double> d((unsigned int)n - 1);

    jint crds[n];
    env->GetIntArrayRegion(coords, 0, n, crds);

    for(unsigned int i = 0; i < n; ++i)
    {
        points[i] = crds[i];
    }

    --n;
    vector<double> a(n);
    vector<double> b(n);
    vector<double> c(n);

    a[0] = 0;
    b[0] = 2;
    c[0] = 1;
    d[0] = points[0] + 2 * points[1];

    for(unsigned int i = 1; i < n - 1; ++i)
    {
        a[i] = 1;
        b[i] = 4;
        c[i] = 1;
        d[i] = 4 * points[i] + 2 * points[i + 1];
    }

    a[n - 1] = 2;
    b[n - 1] = 7;
    c[n - 1] = 0;
    d[n - 1] = 8 * points[n - 1] + points[n];

    for(unsigned int i = 1; i < n; i++)
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

    for(unsigned int i = 0; i < n - 1; i++)
    {
        p2[i] = 2 * points[i + 1] - p1[i + 1];
    }
    p2[n - 1] = 0.5 * (points[n] + p1[n - 1]);

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
        jintArray coords) {
    // algorithm from https://www.particleincell.com/2012/bezier-splines/

    using namespace std;
    vector<double> points((unsigned int)n);
    vector<double> p1((unsigned int)n - 1);
    vector<double> p2((unsigned int)n - 1);
    vector<double> d((unsigned int)n - 1);

    jint crds[n];
    env->GetIntArrayRegion(coords, 0, n, crds);

    for(unsigned int i = 0; i < n; ++i)
    {
        points[i] = crds[i];
    }

    --n;
    vector<double> a((unsigned int)n);
    vector<double> b((unsigned int)n);
    vector<double> c((unsigned int)n);

    a[0] = 0;
    b[0] = 2;
    c[0] = 1;
    d[0] = points[0] + 2 * points[1];

    for(unsigned int i = 1; i < n - 1; ++i)
    {
        a[i] = 1;
        b[i] = 4;
        c[i] = 1;
        d[i] = 4 * points[i] + 2 * points[i + 1];
    }

    a[n - 1] = 2;
    b[n - 1] = 7;
    c[n - 1] = 0;
    d[n - 1] = 8 * points[n - 1] + points[n];

    for(unsigned int i = 1; i < n; i++)
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

    for(unsigned int i = 0; i < n - 1; i++)
    {
        p2[i] = 2 * points[i + 1] - p1[i + 1];
    }
    p2[n - 1] = 0.5 * (points[n] + p1[n - 1]);

    jdoubleArray result = env->NewDoubleArray(n);
    jdouble buf[n];

    for(unsigned int i = 0; i < n; ++i)
    {
        buf[i] = p2[i];
    }

    env->SetDoubleArrayRegion(result, 0, n, buf);

    return result;
}

// astar

inline double distanceEmp(const int a[2], const int b[2], const int emp)
{
    // manhattan Dist
    if(emp == 1)
    {
        return abs(a[0] - b[0]) + abs(a[1] - b[1]);
    }
        // euclid Dist
    else if(emp == 2)
    {
        return sqrt((a[0] - b[0]) * (a[0] - b[0]) + (a[1] - b[1]) * (a[1] - b[1]));
    }

    // INT_MAX Dist
    return (INT_MAX);
}

inline int arrInd(const int & m, const int x[2])
{
    return m * x[0] + x[1];
}

inline int arrInd(const int & m, const std::pair<int, int> & x)
{
    return m * x.first + x.second;
}

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
        jint pixel_size,
        jint map_x,
        jint map_y) {

    // for bitmap x is x and y is y
    // for graph x is y and y is x

    AndroidBitmapInfo  mapInfo;
    void             * ptr = nullptr;

    using namespace std;

    AndroidBitmap_getInfo(env, bitmap, &mapInfo);
    AndroidBitmap_lockPixels(env, bitmap, &ptr);

    int n = map_y;
    int m = map_x;
    int x[2] = {start_y, start_x};
    int y[2] = {finish_y, finish_x};

    priority_queue<
            pair<pair<int, int>, pair<int, int>>,
            vector<pair<pair<int, int>, pair<int, int>>>,
            greater<>
    > pq;

    vector<vector<bool>> g((unsigned int)n, vector<bool>((unsigned int)m, false));
    vector<int> p((unsigned int)n * m, (INT_MAX));
    vector<int> d((unsigned int)n * m, (INT_MAX));

    auto * src = (uint32_t*)ptr;

    for(unsigned int i = 0; i < map_x; ++i)
    {
        for(unsigned int j = 0; j < map_y; ++j)
        {
            int ind = mapInfo.width + 1 + i * pixel_size + j * pixel_size * pixel_size * map_x;
            int clr = src[ind];
            int color = (unsigned int)clr & 0x00FFFFFF;

            if(color == 0x00000000)
            {
                g[j][i] = true;
            }
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);

    pq.push({{distanceEmp(x, y, empirics), 0}, {x[0], x[1]}});
    p[arrInd(m, x)] = -1;
    d[arrInd(m, x)] = 0;

    while(!pq.empty())
    {
        int v[2] = {pq.top().second.first, pq.top().second.second};
        int v_d = pq.top().first.second;
        pq.pop();

        if(v[0] == y[0] && v[1] == y[1]) { break; }

        if(v_d > d[arrInd(m, v)]) { continue; }

        for(int i = 0; i < 8; ++i)
        {
            int u[2] = {v[0], v[1]};
            int u_d = v_d + 1;

            if(i == 0) { ++u[0]; } else
            if(i == 1) { --u[0]; } else
            if(i == 2) { ++u[1]; } else
            if(i == 3) { --u[1]; }
            else if(i == 4 && directions > 1) {
                --u[0];
                --u[1];
            }
            else if(i == 5 && directions > 1) {
                ++u[0];
                --u[1];
            }
            else if(i == 6 && directions > 1) {
                --u[0];
                ++u[1];
            }
            else if(i == 7 && directions > 1) {
                ++u[0];
                ++u[1];
            }

            if(0 > u[0] || u[0] >= n ||
               0 > u[1] || u[1] >= m ||
               g[u[0]][u[1]] ||
               (i > 3 && directions == 1) ||
               (i > 3 && directions == 3 && (g[v[0]][u[1]] || g[u[0]][v[1]]))) { continue; }

            bool a = g[v[0]][u[1]];
            bool b = g[u[0]][v[1]];

            if(u_d < d[arrInd(m, u)])
            {
                pq.push({{u_d + distanceEmp(u, y, empirics), u_d}, {u[0], u[1]}});
                p[arrInd(m, u)] = arrInd(m, v);
                d[arrInd(m, u)] = u_d;
            }
        }
    }

    vector<pair<int, int>> res(0);

    if(p[arrInd(m, {y[0], y[1]})] != (INT_MAX))
    {
        res.push_back({y[0], y[1]});

        for(int i = 0; p[arrInd(m, res[i])] != -1; ++i)
        {
            int s = p[arrInd(m, res[i])];
            res.push_back({s / m, s % m});
        }

        reverse(res.begin(), res.end());
    }

    jint buf[res.size()];

    for(unsigned long long i = 0; i < res.size(); ++i)
    {
        buf[i] = map_x * res[i].first + res[i].second;
    }

    jintArray result = env->NewIntArray(res.size());
    env->SetIntArrayRegion(result, 0, res.size(), buf);

    return result;
}
