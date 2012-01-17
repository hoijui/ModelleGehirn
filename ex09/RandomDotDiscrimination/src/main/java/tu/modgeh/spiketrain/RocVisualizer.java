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

public class RocVisualizer {

	private List<RateGuesser> rateGuessers;

	public RocVisualizer(List<RateGuesser> rateGuessers) {
		this.rateGuessers = rateGuessers;
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

		axis = new NumberAxis("alpha");
		axis.setAutoRange(true);
//		axis.setLowerBound(0.0);
//		axis.setUpperBound(1.0);

		return axis;
	}

	private static ValueAxis createRangeAxis() {

		NumberAxis axis = null;

		axis = new NumberAxis("beta");
		axis.setAutoRange(true);

		return axis;
	}

	public ChartPanel createCanvas() {

		ChartPanel panel = null;

		final XYSeriesCollection stateGuessesSeriesCol = new XYSeriesCollection();

		for (RateGuesser rateGuesser : rateGuessers) {
			XYSeries roc = new XYSeries("d = " + rateGuesser.getRateGenerator().getD());
			for (int z = 0; z < 140; z++) {
				double alpha = rateGuesser.getRateGenerator().calcAlpha(z);
				double beta = rateGuesser.getRateGenerator().calcBeta(z);
				roc.add(alpha, beta);
			}
			stateGuessesSeriesCol.addSeries(roc);
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

		final JFrame mainFrame = new JFrame("ROC curves");
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
