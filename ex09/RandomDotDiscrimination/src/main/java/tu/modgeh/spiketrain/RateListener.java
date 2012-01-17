package tu.modgeh.spiketrain;


/**
 *
 */
public interface RateListener {

	void rateChanged(RateChangedEvent evt);
	void forcedRate(ForcedRateEvent evt);
}
