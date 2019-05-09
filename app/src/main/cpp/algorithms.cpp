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
drawLines();

// parameters: map, start position, finish position
// return: shortest way by A-Star

inline double distanceEmp(const int a[2], const int b[2], const int emp)
{
    if(emp == 1)
    {
        return abs(a[0] - b[0]) + abs(a[1] - b[1]);
    }
    else if(emp == 2)
    {
        return sqrt((a[0] - b[0]) * (a[0] - b[0]) + (a[1] - b[1]) * (a[1] - b[1]));
    }

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

extern "C" JNIEXPORT void JNICALL
algorithmAStar(
        JNIEnv *env,
        jobject obj,
        jobject bitmap,
        jint start_x,
        jint start_y,
        jint finish_x,
        jint finish_y,
        jint empirics,
        jint directions) {

    AndroidBitmapInfo  mapInfo;
    void             **ptr = nullptr;
    int                ret = 0;

    using namespace std;

    AndroidBitmap_getInfo(env, bitmap, &mapInfo);
    ret = AndroidBitmap_lockPixels(env, bitmap, ptr);

    int n = mapInfo.height;
    int m = mapInfo.width;
    int x[2] = {start_y, start_x};
    int y[2] = {finish_y, finish_x};

    priority_queue<
            pair<pair<int, int>, pair<int, int>>,
                    vector<pair<pair<int, int>, pair<int, int>>>,
                            greater<pair<pair<int, int>, pair<int, int>>>
                            > pq;
    vector<int> p(n * m, (INT_MAX));
    vector<int> d(n * m, (INT_MAX));

    pq.push({{distanceEmp(x, y, empirics), 0}, {x[0], x[1]}});
    p[arrInd(m, x)] = -1;
    d[arrInd(m, x)] = 0;

    while(pq.size())
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
            if(i == 3) { --u[1]; } else
            if(i == 4 && directions > 1) {
                --u[1];
            }
            else
            if(i == 5 && directions > 1) {
                --u[1];
            }
            else
            if(i == 6 && directions > 1) {
                --u[1];
            }
            else
            if(i == 7 && directions > 1) {
                --u[1];
            }

            if(0 > u[0] || u[0] >= n ||
               0 > u[1] || u[1] >= m ||
               g[u[0]][u[1]] == -1) { continue; }

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



    AndroidBitmap_unlockPixels(env, bitmap);
}
