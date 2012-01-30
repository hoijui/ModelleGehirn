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


function set_xi_beam_horiz(i_0, b)
	global N_x;
	global N_y;
	global photoRez;
	global xiRangeX;
	global xiRangeY;

	#photoRez = ones(columns(photoRez), rows(photoRez));
	#photoRez(i_0:i_0+b-1, :) = - ones(b, rows(photoRez));
	photoRez = ones(columns(photoRez), rows(photoRez));
	photoRez(:, i_0:i_0+b-1) = - ones(columns(photoRez), b);
endfunction


function set_xi_beam_rotated(theta, b)
	global N_x;
	global N_y;
	global photoRez;
	global xiRangeX;
	global xiRangeY;

	#photoRez = - ones(columns(photoRez), rows(photoRez));
	photoRez = - ones(rows(photoRez), columns(photoRez));

	limit = (b - 1) / 2;
	for x = 1 : rows(photoRez)
		for y = 1 : columns(photoRez)
			val = y * cos(theta) - x * sin(theta);
			if val >= -limit && val <= limit
				photoRez(x, y) = 1;
			endif
		endfor
	endfor
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

function _D = calc_D(sigmaX, sigmaY, k, theta)
	global N_x;
	global N_y;
	global uRangeX;
	global uRangeY;
	_D = 0;
	for x = -uRangeX:uRangeX
		for y = -uRangeY:uRangeY
			cur_d = (1 / (2*pi*sigmaX*sigmaY)) * exp(-(x^2 / (2*sigmaX^2)) -(y^2 / (2*sigmaY^2))) * cos(k*x - theta) * get_xi(x, y);
			set_u(x, y, cur_d);
			_D += cur_d;
		endfor
	endfor
endfunction

exec_10_1_a = true;
exec_10_1_b = true;
exec_10_1_c = true;
exec_10_1_d = false;
exec_10_2   = true;


if exec_10_1_a
	set_N_y(15);

	calc_V();

	surf(-uRangeY:uRangeY, -uRangeX:uRangeX, gangliaCells);
	title('Receptive Field');
	xlabel("N_y");
	ylabel("3*N_x");
	print('receptiveField.png');
endif


if exec_10_1_b
	set_N_y(15);
	b = 5;
	i_0s = 1 : rows(photoRez) - b;
	Vs = [];


	for i_0 = i_0s
		set_xi_beam_horiz(i_0, b);
		Vs(end+1) = calc_V();
	endfor

	plot(i_0s, Vs);
	title('Phase sensitivity');
	xlabel("i_0");
	ylabel("V(i_0)");
	print('phaseSensitivity.png');
endif


if exec_10_1_c
	set_N_y(15);
	b = 5;
	thetas = 0 : 10 : 180;
	Vs = [];

	for theta = thetas
		set_xi_beam_rotated(theta, b);
		Vs(end+1) = calc_V();
	endfor

	plot(thetas, Vs);
	title('Orientation selectivity');
	xlabel("theta");
	ylabel("V(theta)");
	print('orientationSelectivity.png');
endif


if exec_10_1_d
	b = 5;
	thetas = 0 : 10 : 180;
	N_ys = [11, 21, 25, 31];

	Vss = [];
	legendV = strcat("L=", num2str(N_ys.'));
	for myNy = N_ys
		set_N_y(myNy);
		Vs = [];
		for theta = thetas
			set_xi_beam_rotated(theta, b);
			Vs(end+1) = calc_V();
		endfor
		Vss(end+1, :) = Vs;
	endfor

	plot(thetas, Vss);
	legend(legendV);
	title('Orientation selectivity in relation to the elongation factor');
	xlabel("theta");
	ylabel("V(theta)");
	print('orientationSelectivityElongationFactor.png');
endif


if exec_10_2
	set_N_y(15);
	b = 5;
	sigmaX = 10; # elongation in x
	sigmaY = 10; # elongation in y
	k = 0.1; # preffered spacial frequency


	# corresponds to 10.1.a
	D = calc_D(sigmaX, sigmaY, k, 0);

	surf(-uRangeY:uRangeY, -uRangeX:uRangeX, gangliaCells);
	title('Gabor Filter - Receptive Field');
	xlabel("N_y");
	ylabel("3*N_x");
	print('gaborFilter_receptiveField.png');

	# corresponds to 10.1.b
	thetas = 0 : 10 : 180;
	Ds = [];

	for theta = thetas
		set_xi_beam_rotated(theta, b);
		Ds(end+1) = calc_D(sigmaX, sigmaY, k, theta);
	endfor

	plot(thetas, Ds);
	title('Gabor filter');
	xlabel("theta");
	ylabel("D(theta)");
	print('gaborFilter_orientationSelectivity.png');
endif
