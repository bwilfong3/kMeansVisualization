// File name: Scatterplot.java
// Author: Benjamin Wilfong
// Project Due Date: Monday, May 9th 2016
// Program Description:
/*
 * This program is a visual implementation of the k-means algorithm.
 * Clicking "initialize" will generate a random data set of 20 points.
 * Clicking "next" will randomly generate k-means based on the user
 * specified k value in the spinner. Clicking next again will cluster
 * the random points into the appropriate cluster mean, the one with
 * the least distance. After this step, you may click next indefinitely
 * until the point do not move anymore. To reset and try again, click
 * initialize. You may also change the k-value at this points.
 * 
 * NOTE: This program MUST be executed in an environment with
 * pointstyles.css (provided). That file contains CSS styles
 * that design the points on the graph. Without it, the data
 * will not make sense.
 */

import java.util.Random;
import java.util.Vector;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
 
 
public class Scatterplot extends Application implements EventHandler<ActionEvent>{

	int k;
	boolean meansShown = false;
	boolean firstClustered = false;
	final Button initialize = new Button("Initialize");               
    final Button next = new Button("Next");
    final Label meansLabel = new Label("K Value (2 to 5 inclusive)");
    //final TextField numMeans = new TextField("3");
    final NumberAxis xAxis = new NumberAxis(0, 21, 1);
    final NumberAxis yAxis = new NumberAxis(0, 21, 1);    
    final Spinner spinner = new Spinner(2,5,3);

    final ScatterChart<Number,Number> sc = 
            new ScatterChart<Number,Number>(xAxis,yAxis);
    XYChart.Series series = new XYChart.Series();
    
    Vector<XYChart.Series> clusterMeans = new Vector<XYChart.Series>();
    Vector<XYChart.Series> clusterPoints = new Vector<XYChart.Series>();
	Vector<XYChart.Data> rawPointData = new Vector<XYChart.Data>();
	Vector<XYChart.Data> originalPoints = new Vector<XYChart.Data>();
	
	/*
	 * This method sets up all of the GUI elements 
	 */
	
    @Override public void start(Stage stage) {
        stage.setTitle("K-Means Scatter Plot");
        
        xAxis.setMinorTickCount(0);
        yAxis.setMinorTickCount(0);

        //xAxis.setLabel("Age (years)");                
        //yAxis.setLabel("Returns to date");
        sc.setTitle("K-Means Visualization"); 
        sc.setLegendVisible(false);
        
        next.setDisable(true);
                    
        sc.setPrefSize(700, 600);
        
        Platform.runLater(new Runnable(){
        	public void run(){initializeContainers();}});
        
        Scene scene  = new Scene(new Group());
        scene.getStylesheets().add("pointstyles.css");
        final VBox vbox = new VBox();
        final HBox hbox = new HBox();
        
        spinner.setMaxWidth(50);

        initialize.setOnAction(this);
        next.setOnAction(this);
        
        hbox.setSpacing(20);
        hbox.getChildren().addAll(initialize, next, meansLabel, spinner);
        
        vbox.getChildren().addAll(sc, hbox);
        hbox.setPadding(new Insets(10, 10, 10, 50));
        
        ((Group)scene.getRoot()).getChildren().add(vbox);
        stage.setScene(scene);
        stage.show();
    }
 
    /*
     * This method creates all of the containers for each series.
     * There is a bug in the JavaFX packages where you cannot remove
     * a series and then add it back later. To combat this, I initialized
     * the maximum amount of series you can have (1 for the initial data,
     * 5 for the maximum amount of means, and 5 for the maximum actual clusters).
     * clusterMeans and clusterPoints are vectors that hold each 5-element series.
     */
    
    private void initializeContainers()
    {	
    	sc.getData().add(series);
        
        for(int i = 0; i < 5; i++){
    		clusterMeans.add(new XYChart.Series());
    		sc.getData().add(clusterMeans.get(i));
        }
        
        for(int i = 0; i < 5; i++){
    		clusterPoints.add(new XYChart.Series());
    		sc.getData().add(clusterPoints.get(i));
        }
        
        series.setName("Random Data Points");
    }
    
    /*
     * This method creates the randomly generated 20 data points
     * and puts them in the 1st series.
     */
    
    private void instantiatePoints()
    {
    	Random rn = new Random();
    	XYChart.Data temp;
    	
    	
    	for (int i = 0; i < 20; i++)
    	{	
    		temp = new XYChart.Data((double)(rn.nextInt(20) + 1),(double)(rn.nextInt(20) + 1));
    		rawPointData.add(temp); // keep a list to move the data later.
    		series.getData().add(temp);
    	}   		
    }
    
    /*
     * This method creates k-number of randomly generated means
     * based off of what the user specifies in the spinner on the GUI.
     * It is called only after the 1st "next" is clicked after
     * initializing a set of random data points.
     */

    private void initializeMeans(){
    	Random rn = new Random();
    	
    	k = (int)spinner.getValue();
    	for(int i = 0; i < k; i++){
    		clusterMeans.get(i).getData() 
    			.add(new XYChart.Data((double)(rn.nextInt(20) + 1), (double)(rn.nextInt(20) + 1))); // initialize the mean point
    		clusterMeans.get(i).setName("Mean " + (i + 1)); // set the title
    	}
    	
    	for(int i = 0; i < k; i++){
    		clusterPoints.get(i).setName("Cluster " + (i + 1));
    	}
    }
    
    /*
     * This method does the actual cluster of each step after the
     * random means have been generated and put on the screen.
     * During the first cluster, there is a flag that is checked
     * to remove the randomly initialized series from the graph. 
     * This flag is also used to avoid reassigning cluster points to
     * the recalculated means, so we can see where each point stands
     * before the means start moving.
     * 
     * During each iteration afterward, the clusters are cleared from
     * the screen and the data is saved into a vector. Then, for each point
     * the distance is calculated for each mean (k-amount), and then the least
     * amount of distance is used to re-cluster that point into the appropriate
     * cluster.
     * 
     * After all that is said and done, if it is not the first iteration, 
     * the means are recalculated. If it IS the first iteration, the means 
     * are not recalculated and the flag is set so the black diamonds won't
     * try to be removed again and the means will actually change each time
     * "next" is clicked.
     */
    
    private void cluster()
    {
    	XYChart.Data point = new XYChart.Data();
    	XYChart.Data minDistMean = new XYChart.Data();
    	Vector<XYChart.Data> means = new Vector<XYChart.Data>();
    	double minDistance = 9999999;
    	double distance;
    	int minIndex = 0;
    	
    	if(!firstClustered)
    	{
    		for (int i = 0; i < sc.getData().get(0).getData().size(); i++)
    			originalPoints.add(sc.getData().get(0).getData().get(i));
    		
    		sc.getData().get(0).getData().clear();
    		
    		//System.out.println("Initial points removed"); // take out the original points from graph
    		
    	}

    	for(int i = 0; i < 5; i++){
    		sc.getData().get(i + 5 + 1).getData().clear(); // empty out the clusters
    	}
    	
    	for(int i = 0; i < k; i++)
    		means.add((XYChart.Data)clusterMeans.get(i).getData().get(0));
    	
    	for(int i = 0; i < 20; i++)
    	{
    		point = originalPoints.get(i);
    		
    		for(int j = 0; j < k; j++)
    		{
    			distance = calculateDistance(point, means.get(j));
    			
    			if(distance < minDistance){
    				minDistance = distance;
    				minDistMean = means.get(j);
    				minIndex = j; // save which cluster it should go to
    			}
    		}
    		
    		/*System.out.println("Minimum distance point for  (" + point.getXValue() + "," + point.getYValue() + ")"
													           + " is ("
													           + minDistMean.getXValue() + "," + minDistMean.getYValue() + ")"
													           + " at " + minDistance);
    		*/
    		//System.out.println("Move this point to cluster " + (minIndex+1));
    		
    		clusterPoints.get(minIndex).getData()
    			.add(new XYChart.Data(point.getXValue(), point.getYValue())); // add the point to the index of the respective mean
    		
    		minDistance = 999999;
    		minIndex = 0;
    		minDistMean = null;
    	}
    	if(firstClustered)
    		recalculateMeans();

    	else
    		firstClustered = true; // don't try to do it again
    }
    
    /*
     * Simple enough, the Euclidean distance is taken between a 
     * random point specified and the mean point in question. I had
     * to add some typecasting here.
     */
    
    private double calculateDistance(XYChart.Data point, XYChart.Data meanPoint)
    {
    	double xDif = (double)point.getXValue() - (double)meanPoint.getXValue();
    	double yDif = (double)point.getYValue() - (double)meanPoint.getYValue();
    	
    	double distance = Math.sqrt(Math.pow(xDif,2) + Math.pow(yDif,2));
    	
    	/*System.out.println("Distance b/t (" + point.getXValue() + "," + point.getYValue() + ")"
    										+ " and ("
    										+ meanPoint.getXValue() + "," + meanPoint.getYValue() + ")"
    										+ " is " + distance);
    	*/
    	return distance;
    }
    
    /*
     * The means are recalculated after the clusters are reassigned.
     * This is simply done by averaging the x and y values of all points
     * within a cluster. The GUI is updated afterward with the new means.
     */
    
    private void recalculateMeans()
    {
    	double xMean = 0, yMean = 0;
    	
    	for(int i = 0; i < k; i++)
    	{
    		for(int j = 0; j < clusterPoints.get(i).getData().size(); j++)
    		{
    			xMean += (double)((XYChart.Data) clusterPoints.get(i).getData().get(j)).getXValue();
    			yMean += (double)((XYChart.Data) clusterPoints.get(i).getData().get(j)).getYValue();
    		}
    		
    		xMean = xMean/clusterPoints.get(i).getData().size();
    		yMean = yMean/clusterPoints.get(i).getData().size();
    		//System.out.println("Recalculated mean for cluster " + (i + 1) + " is (" 
    		//				   + xMean + "," + yMean + ")");
    		
    		
    		clusterMeans.get(i).getData().remove(0);
    		clusterMeans.get(i).getData().clear();
    		try{
    		clusterMeans.get(i).getData() 
    			.add(new XYChart.Data(xMean, yMean));
    		}catch(IllegalArgumentException e){
    			System.out.println("Error updating means");
    		}
    		
    		xMean = 0;
    		yMean = 0;
    	}
    	
    	
    }
    
    /*
     * This method handles what happens when you click a button.
     * When you click initialize, it will generate a random data 
     * set of 20 points. When you click next, whatever k value is specified
     * will generate k random means. After you click next again, the program
     * will cluster based on those means, then recalculate the means. The k
     * value will not be taken into account until you click "initialize" again.
     */
	@Override
	public void handle(ActionEvent arg0) {
		Platform.runLater(new Runnable(){
        	public void run(){
				if(arg0.getSource() == initialize)
				{
			    	for(int i = 0; i < sc.getData().size(); i++)
			    		sc.getData().get(i).getData().clear(); // clean out clusters and initial data
			    	originalPoints.clear();
					meansShown = false;
					firstClustered = false;
					instantiatePoints();
					next.setDisable(false);
		
		
				}
				else{
					if(!meansShown){
						initializeMeans();
						meansShown = true;}
					else
						cluster();
				}
        	}});
	}
    
	/*
	 * Launches the application.
	 */
    public static void main(String[] args) {
        launch(args);
    }


}