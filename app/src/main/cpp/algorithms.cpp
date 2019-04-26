//
// Created by herman on 26.04.19.
//

#include "algorithms.h"

// parameters: original pic, angle of rotation
// return: turned on specified angle picture

void pictureTurning();

// parameters: original pic, ...
// return: color corrected picture

void pictureColorcorrection();

// parameters: original pic, scale ratio
// return: scaled on specified ratio picture

void pictureScaling();

// parameters: original pic, ...
// return: ...

void pictureSegmentation();

// parameters: original pic, retouching parameters
// return: retouched pic

void pictureRetouching();

// parameters: original pic, retouching parameters
// return: masked pic

void pictureUnsharpMasking();

// parameters: original pic, bilinear algorithm parameters
// return: processed pic

void pictureBilinearFiltration();

// parameters: original pic, trilinear algorithm parameters
// return: processed pic

void pictureTrilinearFiltration();

// parameters: original pic (clear pic), positions of points
// return: interpolated with splines broken line on original pic

void drawLines();

// parameters: map, start position, finish position
// return: shortest way by A-Star

void algotihmAStar();
