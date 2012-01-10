package tu.modgeh.spiketrain;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpikeTrain {

	/** [ms] */
	private int T_s = 10000000;
	/** [ms] */
	private int tau;
	/** [Hz] */
	private int r = 50;
	/**  */
	private List<Boolean> S;
	/**  */
	private Random random = new Random();

	public SpikeTrain(int tau) {

		this.tau = tau;
		this.S = new ArrayList<Boolean>(T_s / tau);
	}

	public void generateTrain() {

		for (int i = 0; i < T_s / tau; i++) {
			S.add((random.nextDouble() * r) < tau);
		}
	}

	/** @param T_b [ms] */
	public double calcH(int T_b, double f) {

		double ln2 = Math.log(2);
		int N = T_s / T_b;

		double H = 0.0;

		// calculate Sum(P_b * log_2(P_b))
		int nSequences = (int) Math.pow(2, T_b / tau);
		int nBits = T_b / tau;
		for (int seq = 0; seq < nSequences; seq++) {
			double P_b = 1.0;
			int bitsLeft = seq;
			// walk through chars
			for (int bit = 0; bit < nBits; bit++) {
				double pSpike;
				double pNoSpike;
				if (f == 0) {
					pSpike = 1.0 / r * tau;
					pNoSpike = 1.0 - pSpike;
				} else {
					double t = tau*0.001*bit;
					double r_max = 40;
					double r_cur = r_max * (Math.sin(2*Math.PI*f*t) + 1);
					pSpike = 1.0 / r_cur * tau;
					pNoSpike = 1.0 - pSpike;
				}
				P_b *= ((bitsLeft & 1) == 1) ? pSpike : pNoSpike;
				bitsLeft = bitsLeft >> 1;
			}
			H += P_b * Math.log(P_b) / ln2;
		}

		H /= -T_b;

		return H * 1000;
	}
}
