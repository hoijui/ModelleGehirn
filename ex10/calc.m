#! /usr/bin/octave -qf
#
# Institution: TU Berlin
# Course:      Modelle zur Informationsverarbeitung im Gehirn - WS2011/2012
# Topic:       Excercise 10 - Hubel-Wiesel-Modell einfacher orientierungsselektiver Zellen
# Author:      Robin Vobruba
#

# Number of ganglia-cells on one axis.
global N_x = 5;
global N_y = -1;

# area of effect in the photo receptros for one ganglia cell
global range = 10;

# Elongationsfaktor
global L = -1;

# Modell-Photorezeptoren xi(i_p, j_p) = [-1 or 1]
global photoRez = [];
global xiRangeX = -1;
global xiRangeY = -1;

# Ganglienzellen (RGC) u(3N_x, N_y)
global gangliaCells = [];
# [1: ON-Zentrum, -1: OFF-Zentrum] see gangliaCells
global gangliaCellsType = [];
global uRangeX = -1;
global uRangeY = -1;

global u_factors = [];


function set_L(new_L)
	global N_x;
	global N_y;
	global L;
	L = new_L;
	N_y = L * N_x;
	initMatrices();
endfunction

function set_N_y(new_N_y)
	global N_x;
	global N_y;
	global L;
	N_y = new_N_y;
	L = N_y / N_x;
	initMatrices();
endfunction

function initMatrices()
	global N_x;
	global N_y;
	global range;
	global photoRez;
	global xiRangeX;
	global xiRangeY;
	global gangliaCells;
	global gangliaCellsType;
	global uRangeX;
	global uRangeY;
	global u_factors;

	uRangeX = (3*N_x - 1) / 2;
	uRangeY = (N_y   - 1) / 2;

	xiRangeX = uRangeX + range;
	xiRangeY = uRangeY + range;

	photoRez = ones(xiRangeX*2 + 1, xiRangeY*2 + 1);

	gangliaCells     = - ones(uRangeX*2 + 1, uRangeY*2 + 1);
	gangliaCellsType = - ones(uRangeX*2 + 1, uRangeY*2 + 1);

	# Set middle 3rd to be ON-center cells
	gangliaCellsType(N_x+1:2*N_x, :) = ones(N_x, columns(gangliaCellsType));

	u_factors = calc_u_factors();
endfunction



function _xi = get_xi(i, j)
	global photoRez;
	global xiRangeX;
	global xiRangeY;
	_xi = photoRez(
			1 + i + xiRangeX,
			1 + j + xiRangeY);
endfunction

function _on = get_isOn(i, j)
	global gangliaCellsType;
	global uRangeX;
	global uRangeY;
	_on = gangliaCellsType(
			1 + i + uRangeX,
			1 + j + uRangeY);
endfunction

function set_u(i, j, u)
	global gangliaCells;
	global uRangeX;
	global uRangeY;
	gangliaCells(
			1 + i + uRangeX,
			1 + j + uRangeY) = u;
endfunction

function _u_factors = calc_u_factors()
	global range;
	_u_factors = [];
	for k = -range:range
		k2 = k^2;
		for l = -range:range
			k2_l2 = k2 + l^2;
			_u_factors(k + range+1, l + range+1) = exp(- k2_l2 / 6) - 1/9 * exp(- k2_l2 / 54);
		endfor
	endfor
endfunction

function _u = calc_u(i, j)
	global u_factors;
	global gangliaCellsType;
	global range;
	filterResults = u_factors .* get_xi(i-range:i+range, j-range:j+range);
	_u = get_isOn(i, j) * sum(filterResults(:));
	set_u(i, j, _u);
endfunction

function _v = calc_v(i, j)
	_v = max(0, calc_u(i, j));
endfunction

function _V = calc_V()
	global N_x;
	global N_y;
	global uRangeX;
	global uRangeY;
	_V = 0;
	for i = -uRangeX:uRangeX
		for j = -uRangeY:uRangeY
			_V += calc_v(i, j);
		endfor
	endfor
	_V *= 1 / (N_x * N_y);
endfunction


exec_10_1_a = true;

if exec_10_1_a
	set_N_y(15);

	calc_V();

	surf(-uRangeY:uRangeY, -uRangeX:uRangeX, gangliaCells);
	title('Receptive Field');
	xlabel("N_y");
	ylabel("3*N_x");
	print('receptiveField.png');
endif








