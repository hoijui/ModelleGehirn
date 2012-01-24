package tu.modgeh.spiketrain;


import java.util.List;
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

public class GuessesVisualizer {

	private List<RateGuesser> rateGuessers;
	private boolean plotAnalytical;
	private boolean plotRocCurveIntegral;

	public GuessesVisualizer(List<RateGuesser> rateGuessers) {

		this.rateGuessers = rateGuessers;
		this.plotAnalytical = false;
		this.plotRocCurveIntegral = false;
	}

	public boolean isPlotAnalytical() {
		return plotAnalytical;
	}

	public void setPlotAnalytical(boolean plotAnalytical) {
		this.plotAnalytical = plotAnalytical;
	}

	public boolean isPlotRocCurveIntegral() {
		return plotRocCurveIntegral;
	}

	public void setPlotRocCurveIntegral(boolean plotRocCurveIntegral) {
		this.plotRocCurveIntegral = plotRocCurveIntegral;
	}

	private static XYItemRenderer createRenderer() {

		DefaultXYItemRenderer renderer = null;

		renderer = new DefaultXYItemRenderer();

		renderer.setDrawSeriesLineAsPath(false);
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseLinesVisible(true);
		renderer.setBaseShapesVisible(true);
		renderer.setBaseShapesFilled(true);

		return renderer;
	}

	private static ValueAxis createDomainAxis() {

		NumberAxis axis = null;

		axis = new NumberAxis("d (discriminability) in Hz");
		axis.setAutoRange(true);
//		axis.setLowerBound(0.0);
//		axis.setUpperBound(1.0);

		return axis;
	}

	private static ValueAxis createRangeAxis() {

		NumberAxis axis = null;

		axis = new NumberAxis("Ratio of correct guesses");
		axis.setAutoRange(true);

		return axis;
	}

	public ChartPanel createCanvas() {

		ChartPanel panel = null;

		final XYSeriesCollection stateGuessesSeriesCol = new XYSeriesCollection();

		XYSeries stateGuessesSeries = new XYSeries("simulated");
		for (RateGuesser rateGuesser : rateGuessers) {
			stateGuessesSeries.add(rateGuesser.getRateGenerator().getD(),
					rateGuesser.getCorrectGuessRatio());
		}
		stateGuessesSeriesCol.addSeries(stateGuessesSeries);

		if (isPlotAnalytical()) {
			XYSeries stateCalculatedSeries = new XYSeries("analytical");
			for (RateGuesser rateGuesser : rateGuessers) {
				stateCalculatedSeries.add(rateGuesser.getRateGenerator().getD(),
						rateGuesser.getRateGenerator().pCorrect());
			}
			stateGuessesSeriesCol.addSeries(stateCalculatedSeries);
		}

		if (isPlotRocCurveIntegral()) {
			XYSeries stateCalculated2Series = new XYSeries("alpha*beta");
			for (RateGuesser rateGuesser : rateGuessers) {
System.out.println("isPlotRocCurveIntegral, d=" + rateGuesser.getRateGenerator().getD());
				stateCalculated2Series.add(rateGuesser.getRateGenerator().getD(),
						rateGuesser.getRateGenerator().calcPCorrectThroughAlphaBeta());
			}
			stateGuessesSeriesCol.addSeries(stateCalculated2Series);
		}

		ValueAxis rangeAxis = createRangeAxis();
		ValueAxis domainAxis = createDomainAxis();

		XYPlot plot = new XYPlot(stateGuessesSeriesCol, domainAxis, rangeAxis, createRenderer());

		plot.setDomainCrosshairVisible(true);

		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		JFreeChart chart = new JFreeChart(plot);
		panel = new ChartPanel(chart);

		return panel;
	}

    public void run() {

		final JFrame mainFrame = new JFrame("\"+\" or \"-\" state guesses");
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setSize(800, 500);

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
