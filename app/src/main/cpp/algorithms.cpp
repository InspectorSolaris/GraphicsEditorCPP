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

extern "C" JNIEXPORT void JNICALL
algotihmAStar();
