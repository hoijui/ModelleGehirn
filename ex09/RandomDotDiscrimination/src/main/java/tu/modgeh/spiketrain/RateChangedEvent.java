package tu.modgeh.spiketrain;


import java.util.EventObject;

/**
 *
 */
public class RateChangedEvent extends EventObject {

	private double newRate;

	public RateChangedEvent(RateGenerator source, double newRate) {
		super(source);

		this.newRate = newRate;
	}

	public double getNewRate() {
		return newRate;
	}
}
