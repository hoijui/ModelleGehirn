#!/bin/sh

convert -delay 30 -loop 1 weights_b_*.png weights_b_animated.gif

convert -delay 30 -loop 1 weights_c_*.png weights_c_animated.gif

