//
// Created by herman on 26.04.19.
//


#include "algorithms.h"

// parameters: original pic, angle of rotation
// return: turned on specified angle picture

extern "C" JNIEXPORT void JNICALL
pictureTurning();

// parameters: original pic, ...
// return: color corrected picture

extern "C" JNIEXPORT void JNICALL
pictureColorcorrection();

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
Java_com_example_graphicseditorcpp_SplinesActivity_drawLines(
        JNIEnv *env,
        jobject obj,
        jint n,
        jintArray x_points,
        jintArray y_points) {
    // algorithm from https://www.particleincell.com/2012/bezier-splines/

    using namespace std;
    vector<pair<double, double>> p1(n);
    vector<pair<double, double>> p2(n);
    vector<pair<double, double>> points(n);
    vector<pair<double, double>> d(n);

    jint pxs[n];
    jint pys[n];
    env->GetIntArrayRegion(x_points, 0, n, pxs);
    env->GetIntArrayRegion(y_points, 0, n, pys);

    for (int i = 0; i < n; i++){
        points[i] = make_pair(pxs[i], pys[i]);
    }
    n--;

    double a[1000], b[1000], c[1000];
    a[0] = 0;
    b[0] = 2;
    c[0] = 1;
    d[0] = (make_pair(points[0].first + 2 * points[1].first, points[0].second + 2 * points[1].second));

    for (int i = 1; i < n - 1; i++){
        a[i] = 1;
        b[i] = 4;
        c[i] = 1;
        d[i] = (make_pair(points[i].first + 2 * points[i + 1].first, points[i].second + 2 * points[i + 1].second));
    }

    a[n - 1] = 2;
    b[n - 1] = 7;
    c[n - 1] = 0;
    d[n - 1] = (make_pair(8 * points[n - 1].first + points[n].first, 8 * points[n - 1].second + points[n].second));

    for (int i = 1; i < n; i++){
        double w = a[i] / b[i-1];
        b[i] = b[i] - w * c[i-1];
        d[i].first = d[i].first - w * d[i - 1].first;
        d[i].second = d[i].second - w * d[i - 1].second;
    }
    p1[n - 1] = make_pair(d[n - 1].first / b[n - 1], d[n - 1].second / b[n - 1]);
    for (int i = n - 2; i >= 0; i--){
        p1[i] = make_pair((d[i].first - c[i] * p1[i + 1].first) / b[i], (d[i].second - c[i] * p1[n - 2 - i].second) / b[i]);
    }
    for (int i = 0; i < n - 1; i++){
        p2[i] = (make_pair(2 * points[i + 1].first - p1[i + 1].first, 2 * points[i + 1].second - p1[i + 1].second));
    }
    p2[n - 1] = (make_pair(0.5 * (points[n].first + p1[n - 1].first), 0.5 * (points[n].second + p1[n - 1].second)));
}

// parameters: map, start position, finish position
// return: shortest way by A-Star

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

extern "C" JNIEXPORT jobjectArray JNICALL
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

    jclass jstring = env->FindClass("java/lang/String");
    jobjectArray result = env->NewObjectArray((jsize)res.size(), jstring, 0);
    jint buf[res.size()];

    for(unsigned long long i = 0; i < res.size(); ++i)
    {
        buf[i] = map_x * res[i].first + res[i].second;
        env->SetObjectArrayElement(result, (jsize)i, env->NewStringUTF(to_string(buf[i]).c_str()));
    }

    return result;
}
