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

	private static final double gaussConst1 = Math.sqrt(2 * Math.PI);
	private double gaussProbability(double mu, double sigma, double x) {
		return 1 / (sigma * gaussConst1) * Math.exp(- 0.5 * Math.pow((x - mu) / sigma, 2));
	}

	private double gaussIntegralTillInfinity(double mu, double sigma, double from) {

		double value = 0.0;

		final double deltaX = 0.01;

		for (double x = from - mu; x < 30.0; x += deltaX) {
			value += deltaX * gaussProbability(0.0, sigma, x);
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

	/**
	 * Start the next simulation step.
	 * @return the new simulation steps fire-rate r [Hz]
	 */
	private void update() {

		// pick a random new state, "+" or "-"
		state = random.nextBoolean();

		double mu = state ? getMuPlus() : getMuMinus();
		r = mu + random.nextGaussian() * getSigma();

		fireRateChanged(new RateChangedEvent(this, r));
	}

	public void addRateListener(RateListener listener) {
		rateListeners.add(listener);
	}

	public void removeRateListener(RateListener listener) {
		rateListeners.remove(listener);
	}

	protected void fireRateChanged(RateChangedEvent rateChangedEvent) {

		for (RateListener rateListener : rateListeners) {
			rateListener.rateChanged(rateChangedEvent);
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
}
