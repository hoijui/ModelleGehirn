package tu.modgeh.spiketrain;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RateGenerator {

	/** for r_- and r_+ [Hz] */
	private int sigma;
	/** <r_-> [Hz] */
	private int muMinus;
	/** <r_+> [Hz] */
	private int muPlus;

	/** discriminability [Hz] */
	private int d;

	/**
	 * false: "-"
	 * true:  "+"
	 */
	private boolean state;
	/** The current fire-rate r [Hz] */
	private double r;

	private boolean randomTest = true;

	/** number of simulation steps  */
	private int nSteps = 10000;
	/**  */
	private Random random = new Random();

	private List<RateListener> rateListeners = new ArrayList<RateListener>();


	/** @param d discriminability [Hz] */
	public RateGenerator(int d) {

		this.d = d;

		this.sigma = 10;
		this.muMinus = 20;
		this.muPlus = calcMuPlus(d);

		this.state = false;
		this.r = -1.0;
	}

	/**
	 * Calculate mu of r_+.
	 * d = (<r_+> - <r_->) / sigma
	 * d = (mu_plus - mu_minus) / sigma
	 * -> mu_plus = d * sigma + mu_minus
	 */
	private int calcMuPlus(int discriminability) {
		return discriminability * getSigma() + getMuMinus();
	}

	private static class IntErfcFunction implements Function {

		public IntErfcFunction() {
		}

		public double calculate(double x) {
			return Math.exp(- Math.pow(x, 2));
		}
	}

	public double pCorrect() {

		return 0.5 * erfc(-d / 2.0);
	}

	private static final double erfcConst1 = 2.0 / Math.sqrt(Math.PI);
	private double erfc(double x) {

		final double infinity = 30.0;

		Function intErfcFunction = new IntErfcFunction();

		return erfcConst1 * integral(intErfcFunction, x, infinity);
	}

	private static class GaussProbabilityFunction implements Function {

		private static final double gaussConst1 = Math.sqrt(2 * Math.PI);

		private double mu;
		private double sigma;

		public GaussProbabilityFunction(double mu, double sigma) {

			this.mu = mu;
			this.sigma = sigma;
		}

		public double calculate(double x) {
			return 1 / (sigma * gaussConst1) * Math.exp(-0.5 * Math.pow((x - mu) / sigma, 2));
		}
	}

	private double gaussIntegralTillInfinity(double mu, double sigma, double from) {

		final double infinity = 30.0;

		Function gaussProbability = new GaussProbabilityFunction(0.0, sigma);

		return integral(gaussProbability, from - mu, infinity);
	}

	private double integral(Function f, double from, double to) {

		double value = 0.0;

		final double deltaX = 0.01;

		for (double x = from; x < to; x += deltaX) {
			value += deltaX * f.calculate(x);
		}

		return value;
	}

	public double calcAlpha(double z) {
		return gaussIntegralTillInfinity(muMinus, sigma, z);
	}

	public double calcBeta(double z) {
		return gaussIntegralTillInfinity(muPlus, sigma, z);
	}

	/**
	 * Runs the whole simulation.
	 */
	public void runSimulation() {

		for (int ss = 0; ss < nSteps; ss++) {
			update();
		}
	}

	private double calcRate(boolean forState) {

		double mu = forState ? getMuPlus() : getMuMinus();
		return mu + random.nextGaussian() * getSigma();
	}

	/**
	 * Calculate the next simulation step.
	 */
	private void updateRandom() {

		// pick a random new state, "+" or "-"
		state = random.nextBoolean();

		fireRateChanged(new RateChangedEvent(this, calcRate(state)));
	}

	/**
	 * Calculate the next simulation step.
	 */
	private void updateForced() {

		double rateMinus = calcRate(false);
		double ratePlus = calcRate(true);

		fireForcedRate(new ForcedRateEvent(this, rateMinus, ratePlus));
	}

	/**
	 * Calculate the next simulation step.
	 */
	private void update() {

		if (isRandomTest()) {
			updateRandom();
		} else {
			updateForced();
		}
	}

	public void addRateListener(RateListener listener) {
		rateListeners.add(listener);
	}

	public void removeRateListener(RateListener listener) {
		rateListeners.remove(listener);
	}

	protected void fireRateChanged(RateChangedEvent evt) {

		for (RateListener rateListener : rateListeners) {
			rateListener.rateChanged(evt);
		}
	}

	protected void fireForcedRate(ForcedRateEvent evt) {

		for (RateListener rateListener : rateListeners) {
			rateListener.forcedRate(evt);
		}
	}

	/**
	 * @return discriminability d [Hz]
	 */
	public int getD() {
		return d;
	}

	/**
	 * Returns the current simulation steps state.
	 * @return false: "-", true: "+"
	 */
	public boolean getState() {
		return state;
	}

	/**
	 * The gaussian sqrt(variation) for r_- and r_+ [Hz]
	 * @return sigma
	 */
	public int getSigma() {
		return sigma;
	}

	/**
	 * <r_-> [Hz]
	 * @return mu_-
	 */
	public int getMuMinus() {
		return muMinus;
	}

	/**
	 * <r_+> [Hz]
	 * @return mu_+
	 */
	public int getMuPlus() {
		return muPlus;
	}

	public boolean isRandomTest() {
		return randomTest;
	}

	public void setRandomTest(boolean randomTest) {
		this.randomTest = randomTest;
	}
}
