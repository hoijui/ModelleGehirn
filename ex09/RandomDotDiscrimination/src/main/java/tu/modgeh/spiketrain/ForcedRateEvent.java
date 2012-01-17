package tu.modgeh.spiketrain;


import java.util.EventObject;

/**
 *
 */
public class ForcedRateEvent extends EventObject {

	private double rateMinus;
	private double ratePlus;

	public ForcedRateEvent(RateGenerator source, double rateMinus, double ratePlus) {
		super(source);

		this.rateMinus = rateMinus;
		this.ratePlus = ratePlus;
	}

	public double getRateMinus() {
		return rateMinus;
	}

	public double getRatePlus() {
		return ratePlus;
	}
}
