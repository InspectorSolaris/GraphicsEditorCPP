//
// Created by herman on 23.05.19.
//

#include "astar.h"

double distanceEmp(
        int a[2],
        int b[2],
        int emp)
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

int arrInd(const int & m, const int x[2])
{
    return m * x[0] + x[1];
}

int arrInd(const int & m, const std::pair<int, int> & x)
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
        jint pixel_size)
{
    AndroidBitmapInfo  info;
    void             * ptr = nullptr;

    using namespace std;

    AndroidBitmap_getInfo(env, bitmap, &info);
    AndroidBitmap_lockPixels(env, bitmap, &ptr);

    int n = info.height;
    int m = info.width;
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

    auto * src = (uint32_t *)ptr;

    for(unsigned int i = 0; i < m; ++i)
    {
        for(unsigned int j = 0; j < n; ++j)
        {
            int ind = m + 1 + i * pixel_size + j * pixel_size * pixel_size * m;
            uint32_t color = src[ind] & 0x00FFFFFFU;

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
               (i > 3 && directions == 3 && (g[v[0]][u[1]] || g[u[0]][v[1]])))
            {
                continue;
            }

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
        res.emplace_back(y[0], y[1]);

        for(int i = 0; p[arrInd(m, res[i])] != -1; ++i)
        {
            int s = p[arrInd(m, res[i])];
            res.emplace_back(s / m, s % m);
        }

        reverse(res.begin(), res.end());
    }

    jint buf[res.size()];

    for(unsigned long long i = 0; i < res.size(); ++i)
    {
        buf[i] = m * res[i].first + res[i].second;
    }

    jintArray result = env->NewIntArray((jint)res.size());
    env->SetIntArrayRegion(result, 0, (jint)res.size(), buf);

    return result;
}