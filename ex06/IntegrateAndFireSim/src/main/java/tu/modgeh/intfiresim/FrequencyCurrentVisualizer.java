package tu.modgeh.intfiresim;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class FrequencyCurrentVisualizer {

	public FrequencyCurrentVisualizer() {
	}

	private static XYItemRenderer createRenderer() {

		DefaultXYItemRenderer renderer = null;

		renderer = new DefaultXYItemRenderer();

		return renderer;
	}

	private static ValueAxis createDomainAxis() {

		NumberAxis axis = null;

		axis = new NumberAxis();
		axis.setAutoRange(true);

		return axis;
	}

	private static ValueAxis createRangeAxis() {

		NumberAxis axis = null;

		axis = new NumberAxis();
		axis.setAutoRange(true);

		return axis;
	}

	public static ChartPanel createCanvas() {

		ChartPanel panel = null;

		final XYSeriesCollection currentFreqsSeries = new XYSeriesCollection();

		final double simulationTime = 50.0;
		final int currents = 100;
		final int sigmas = 10;
		final double sigmaMax = 3.0;

		final double deltaSigma = sigmaMax / sigmas;
		ExecutorService executor = Executors.newFixedThreadPool(5);
System.out.println("6.b) simulating ...");
		for (double sigma = 0.0; sigma <= sigmaMax; sigma += deltaSigma) {
			final double curSigma = (double)Math.round(sigma * sigmas) / sigmas;
			sigma = curSigma;
			final Integrator integrator = new Integrator();
			integrator.setNoiseStandardDeviation(sigma);
			executor.submit(new Runnable() {
				@Override
				public void run() {
					FreqencyCurrentSeriesCreator serGen = new FreqencyCurrentSeriesCreator(integrator, simulationTime, currents);
					XYSeries currentFreqs;
					try {
System.out.println("\tsigma: " + curSigma + " ...");
						currentFreqs = serGen.call();
						currentFreqs.setKey(/*spikes.getKey() + "   " + */"sigma: " + curSigma);
						currentFreqsSeries.addSeries(currentFreqs);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		}
		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		ValueAxis rangeAxis = createRangeAxis();
		ValueAxis domainAxis = createDomainAxis();

		XYPlot plot = new XYPlot(currentFreqsSeries, domainAxis, rangeAxis, createRenderer());

		plot.setDomainCrosshairVisible(true);

		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		JFreeChart chart = new JFreeChart(plot);
		panel = new ChartPanel(chart);

		return panel;
	}

    public void run() {

		final JFrame mainFrame = new JFrame("6.b) Frequency-Current spike-rates");
		mainFrame.setSize(800, 600);

		ChartPanel canvas = createCanvas();
		canvas.setBorder(new EmptyBorder(10, 10, 10, 10));

		mainFrame.getContentPane().add(canvas);

		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainFrame.setVisible(true);
			}
		});
	}
}
