#! /usr/bin/octave -qf
#
# Institution: TU Berlin
# Course:      Modelle zur Informationsverarbeitung im Gehirn - WS2011/2012
# Topic:       Excercise 11 - Entwicklung einer einfachen Zelle durch Hebb-Lernen
# Author:      Robin Vobruba
#

# Number of photo receptor cells on each half-axis.
# The total number of cells is: (xi_a*2 + 1)^2
global xi_a = 20;

# Number of retinal cells on each half-axis.
# The total number of cells is: (v_a*2 + 1)^2
global v_a = 6;

# The base factor for gaussian radii of xi influencing v.
global R = -1;
global R_inf_a = -1;

# learning rate eta
global eta = -1;

# photo receptors xi(i_p, j_p) = [-1 or 1]
global xi = zeros(xi_a*2 + 1, xi_a*2 + 1);

# retinal cells (RGC) v(i, j)
global v = zeros(v_a*2 + 1, v_a*2 + 1);

# weights between retina cells v and the cortical neuron (output) w(i, j)
global w = zeros(columns(v), rows(v));

global v_factors = [];


function set_R(new_R)
	global R;
	global R_inf_a;
	global v_factors;
	R = new_R;
	R_inf_a = ceil(6*R);
	v_factors = calc_v_factors();
endfunction

function init_W()
	global w;
	w = (0.2 .* rand(columns(w), rows(w))) .- 0.1;
endfunction

function set_xi_random()
	global xi;
	xi = (2 .* rand(columns(xi), rows(xi))) .- 1;
	calc_V();
endfunction


function _xi = get_xi(i, j)
	global xi;
	global xi_a;
	_xi = xi(
			1 + i + xi_a,
			1 + j + xi_a);
endfunction

function set_v(i, j, vVal)
	global v;
	global v_a;
	v(
			1 + i + v_a,
			1 + j + v_a) = vVal;
endfunction

function _v_factors = calc_v_factors()
	global R;
	global R_inf_a;
	_v_factors = [];
	R2 = R^2;
	R2_2  = R2*2;
	R2_18 = R2*18;
	for k = -R_inf_a:R_inf_a
		k2 = k^2;
		for l = -R_inf_a:R_inf_a
			k2_l2 = k2 + l^2;
			_v_factors(k + R_inf_a + 1, l + R_inf_a + 1) = exp(- k2_l2 / R2_2) - 1/9 * exp(- k2_l2 / R2_18);
		endfor
	endfor
endfunction

function calc_v(i, j)
	global v_factors;
	global R_inf_a;
	global xi;
	global xi_a;
	global v;
	global v_a;
	vSummandMatrix = v_factors .* xi(i+xi_a+1-R_inf_a:i+xi_a+1+R_inf_a, j+xi_a+1-R_inf_a:j+xi_a+1+R_inf_a);
	v(i + v_a + 1, j + v_a + 1) = sum(vSummandMatrix(:));
endfunction

function calc_V()
	global v_a;
	for i = -v_a:v_a
		for j = -v_a:v_a
			calc_v(i, j);
		endfor
	endfor
endfunction

function _s = calc_s()
	global v;
	global w;
	sMatrix = v .* w;
	_s = sum(sMatrix(:));
endfunction

function update_W()
	global v;
	global w;
	global eta;
	s = calc_s();
	w2 = w^2;
	w = w + eta * (s*v - 0.1*w2*w);
endfunction

function train(myR, myEta, myFileDef, myDesc)
	global eta;
	global w;
	global v_a;
	eta = myEta;
	set_R(myR);
	init_W();

	myPlt = surf(-v_a:v_a, -v_a:v_a, w);
	set(myPlt, "zdatasource", "w");
	pltTitle = 'Weights';
	title(pltTitle);
	axis([-v_a, v_a, -v_a, v_a, -10, 10]);
	print();

	n = 500;
	for tr = 1:n+1
		if mod(tr, 100) == 0 || tr == 1 || tr == n+1
			filePostfix = cstrcat(myFileDef, '_', num2str(tr));
			pltTitle = cstrcat('Weights (iter: ', num2str(tr - 1), ', R: ', num2str(myR), ', eta: ', num2str(eta), ') ', myDesc);
			title(pltTitle);
			pause(0.001);
			refreshdata(gcf(), "caller");
			print(cstrcat('weights_', filePostfix, '.png'));
		endif
		tr
		set_xi_random();
		update_W();
		if mod(tr, 5) == 0
			pltTitle = cstrcat('Weights (iter: ', num2str(tr), ', R: ', num2str(myR), ', eta: ', num2str(eta), ') ', myDesc);
			title(pltTitle);
			pause(0.001);
			refreshdata(gcf(), "caller");
		endif
	endfor
endfunction

exec_11_1_b = true;
exec_11_1_c = true;

if exec_11_1_b
	train(1.7, 0.001, 'b', '(11.1.b)');
endif

if exec_11_1_c
	train(1.2, 0.001, 'c', '(11.1.c)');
endif

