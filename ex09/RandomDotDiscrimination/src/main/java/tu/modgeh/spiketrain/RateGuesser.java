package tu.modgeh.spiketrain;

import java.util.LinkedList;
import java.util.List;


public class RateGuesser implements RateListener {

	/*public static class Guess {

		private boolean stateReal;
		private boolean stateGuessed;

		public Guess(boolean stateReal, boolean stateGuessed) {

			this.stateReal = stateReal;
			this.stateGuessed = stateGuessed;
		}

		public boolean getStateReal() {
			return stateReal;
		}

		public boolean getStateGuessed() {
			return stateGuessed;
		}

		public boolean isGuessCorrect() {
			return (stateReal == stateGuessed);
		}
	}*/

	/**  */
	private RateGenerator rateGenerator;

	/** threshold [Hz] (whether we assume to see r_- or r_+ */
	private double z;

	/**  */
//	private List<Guess> guesses = new LinkedList<Guess>();
	private int correctGuesses;
	private int incorrectGuesses;


	public RateGuesser(RateGenerator rateGenerator) {

		this.rateGenerator = rateGenerator;
		this.z = calcZ_91a();

		rateGenerator.addRateListener(this);
	}

	/**
	 * d = (<r_+> - <r_->) / sigma
	 * d = (mu_plus - mu_minus) / sigma
	 * -> mu_plus = d * sigma + mu_minus
	 */
	private double calcZ_91a() {
		return (rateGenerator.getMuMinus() + rateGenerator.getMuPlus()) / 2.0;
	}

	@Override
	public void rateChanged(RateChangedEvent evt) {
//		guesses.add(new Guess(
//				((RateGenerator)evt.getSource()).getState(),
//				guessState(evt.getNewRate())
//				));

		boolean correctState = ((RateGenerator)evt.getSource()).getState();
		boolean guessedState = guessState(evt.getNewRate());
		if (guessedState == correctState) {
			correctGuesses++;
		} else {
			incorrectGuesses++;
		}
	}

	private boolean guessState(double r) {
		return (r >= z);
	}

	public RateGenerator getRateGenerator() {
		return rateGenerator;
	}

//	public List<Guess> getGuesses() {
//		return guesses;
//	}

	public int getCorrectGuesses() {
		return correctGuesses;
	}

	public int getIncorrectGuesses() {
		return incorrectGuesses;
	}

	public double getCorrectGuessRatio() {
		return (double)correctGuesses / (correctGuesses + incorrectGuesses);
	}
}
