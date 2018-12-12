package bpmnMetadataExtractor;

import java.util.ArrayList;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.di.Edge;
import org.camunda.bpm.model.bpmn.instance.di.Shape;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
/**
 * Class that contains the methods to calculate the Layout Measure metric.
 * For this metric the following weights have been used:
 * Non-Rectilinear sequence flow = 1;
 * Intersecting sequence flows = 1;
 * @author PROSLabTeam
 *
 */
public class LayoutMetricsExtractor {
	
	private BpmnBasicMetricsExtractor basicExtractor;
	private Collection<ModelElementInstance> edges;
	private Collection<ModelElementInstance> shapes;
	
	public LayoutMetricsExtractor(BpmnBasicMetricsExtractor basicMetricsExtractor) {
		this.basicExtractor = basicMetricsExtractor;
		this.edges = basicExtractor.getCollectionOfElementType(BpmnEdge.class);
		this.shapes = basicExtractor.getCollectionOfElementType(BpmnShape.class);
	}
	
	public int getLayoutMeasure(){
		return this.getWaypointsMeasures(edges); // + this.getShapesMeasures(shapes) 
	}
	
	private class Segment{
		private Waypoint w1;
		private Waypoint w2;
		
		public Segment(Waypoint w1, Waypoint w2){
			this.w1 = w1;
			this.w2 = w2;
		}

		@Override
		public boolean equals(Object obj){
			if (obj == null) 
				return false;
			if (obj == this) 
				return true;
			if (!(obj instanceof Segment)) 
				return false;
			Segment other = (Segment) obj;
			if (this.w1 == other.w1 && this.w2 == other.w2)
				return true;
			else
				return false;
		}
	}
	
	private int getWaypointsMeasures(Collection<ModelElementInstance> edges){
		int toReturn = 0;
		ArrayList<Segment> segments = new ArrayList();
		
		for (ModelElementInstance e: edges){
		
			ArrayList<Waypoint> waypoints = new ArrayList();
			waypoints.addAll(((Edge) e).getWaypoints());
			for (int i = 0; i + 1 < waypoints.size(); i++){
				segments.add(new Segment(waypoints.get(i), waypoints.get(i + 1)));
			}
			
			if (((Edge)e).getWaypoints().size() > 2) //Check if there are non-rectilinear sequence flows
				toReturn ++;
		}
		
		return this.checkIntersections(segments)/2 + toReturn;
	}
	/**
	 * Check if there are intersection in the model
	 * It calls methods checkSharedVertex and isIntersected
	 * @param segments
	 * @return
	 */
	private int checkIntersections(ArrayList<Segment> segments){
		int numberOfIntersections = 0;
		for (Segment s: segments){
			Segment firstSegment = s;
			for (Segment t: segments){
				Segment secondSegment = t;
				if (!(firstSegment.equals(secondSegment))){
					if (!this.checkSharedVertex(firstSegment, secondSegment)){
						if(this.isIntersected(firstSegment.w1, firstSegment.w2, secondSegment.w1, secondSegment.w2))
							numberOfIntersections ++;
					}
				}
					
			}
		}
		return numberOfIntersections;
	}
	
	/**
	 * Check where or not the point w3 lies on the [w1,w2] segment
	 * @param w1
	 * @param w2
	 * @param w3
	 * @return true if w3 lies on the [w1,w2] segment
	 */
	private boolean pointOnSegment(Waypoint w1, Waypoint w3, Waypoint w2){
		double w1x = w1.getX();
		double w1y = w1.getY();
		double w2x = w2.getX();
		double w2y = w2.getY();
		double w3x = w3.getX();
		double w3y = w3.getY();
		
		if (w3x <= Math.max(w1x, w2x) && w3x >= Math.min(w1x, w2x) && 
			w3y <= Math.max(w1y, w2y) && w3y >= Math.min(w1y,  w2y))
				return true;
		return false;
	}
	
	/**
	 * check if w3 shares the same segment [w1,w2]
	 * @param w1
	 * @param w2
	 * @param w3
	 * @return
	 */
	private int orientation(Waypoint w1, Waypoint w3, Waypoint w2){
		double w1x = w1.getX();
		double w1y = w1.getY();
		double w2x = w2.getX();
		double w2y = w2.getY();
		double w3x = w3.getX();
		double w3y = w3.getY();
		double val = (w3y - w1y) * (w2x - w3x) - (w3x - w1x) * (w2y - w3y);
		
	if ( val == 0)  //colinear
		return 0;
	
	return (val > 0) ? 1: 2; //clock or counterclock wise
	
	}
	/**
	 * returns true if the two segments intersect each other
	 * @param w1 first segment waypoint
	 * @param w2 first segment waypoint
	 * @param w3 second segment waypoint
	 * @param w4 second segment waypoint
	 * @return
	 */
	private boolean isIntersected(Waypoint w1, Waypoint w2, Waypoint w3, Waypoint w4){
		int o1 = this.orientation(w1, w2, w3);
		int o2 = this.orientation(w1, w2, w4);
		int o3 = this.orientation(w3, w4, w1);
		int o4 = this.orientation(w3, w4, w2);
		boolean intersected = false;
		
		if (o1 != o2 && o3 != o4){
			intersected = true;
		}
//		Check if the two segmenets are colinear and they intersect
//		if (o1 == 0 && this.pointOnSegment(w1, w2, w3)) {
//			intersected = true;
//			return intersected;
//		}
//
//		
//		if (o2 == 0 && this.pointOnSegment(w1, w2, w4)) {
//			intersected = true;
//			return intersected;			
//		}
//		
//		if (o3 == 0 && this.pointOnSegment(w3, w4, w1)) {
//			intersected = true;
//			return intersected;	
//		}
//		
//		if (o4 == 0 && this.pointOnSegment(w3, w4, w2)) {
//			intersected = true;
//			return intersected;
//	}
		return intersected;
	}
	/**
	 * Check if the same vertex is shared by two segments
	 * @param s1	first segment
	 * @param s2	second segment
	 * @return
	 */
	private boolean checkSharedVertex(Segment s1, Segment s2){
		double wp1x = s1.w1.getX();
		double wp1y = s1.w1.getY();
		double wp2x = s1.w2.getX();
		double wp2y = s1.w2.getY();
		double wp3x = s2.w1.getX();
		double wp3y = s2.w1.getY();
		double wp4x = s2.w2.getX();
		double wp4y = s2.w2.getY();

		if (((wp1x == wp3x && wp1y == wp3y) || (wp1x == wp4x && wp1y == wp4y)) 
		||  ((wp2x == wp3x && wp2y == wp3y) || (wp2x == wp4x && wp2y == wp4y)))
			return true;
		
		return false;
	}
	
	private int getShapesMeasures(Collection<ModelElementInstance> shapes){
		int toReturn = 0;
		for (ModelElementInstance s: shapes){
			BpmnShape firstShape = (BpmnShape) s;	
			for (ModelElementInstance t: shapes){
				BpmnShape secondShape = (BpmnShape) t;
				if (!firstShape.equals(secondShape)) {
					if(this.isOverlapped(firstShape, secondShape))
						toReturn++;
				}
			}
			
		}
		System.out.println("Figure che si overlappano :" + toReturn);
		return toReturn;
	}
	
	/**
	 * Method which check wheter or not two different shapes are overlapped
	 * The center of the shapes is the position of the object
	 * TODO Generalizzare le coordinate degli elementi nel modello in modo che le coordinate riflettano il punto centrale delle figure
	 * @param s1
	 * @param s2
	 * @return
	 */
	private boolean isOverlapped(BpmnShape s1, BpmnShape s2){
		System.out.println(s1);
		System.out.println(s2);
		System.out.println(s1.equals(s2));
		double firstX = s1.getBounds().getX();
		double firstY = s1.getBounds().getY();
		double firstHeight = s1.getBounds().getHeight() / 2;
		double firstWidth = s1.getBounds().getWidth() / 2;
		
		double secondX = s2.getBounds().getX();
		double secondY = s2.getBounds().getY();
		double secondHeight = s2.getBounds().getHeight() / 2;
		double secondWidth = s2.getBounds().getWidth() / 2;
		
		if (firstX > secondX && firstY > secondY){
			//Seconda figura in alto a destra
			if (firstX + firstWidth > secondX - secondWidth && firstY - firstHeight < secondY + secondHeight)
				return true;
		}
		
		if (firstX > secondX && firstY < secondY){
			//seconda figura in basso a destra
			if (firstX + firstWidth > secondX - secondWidth && firstY + firstHeight > secondY - secondHeight)
				return true;
		}
		
		if (firstX < secondX && firstY > secondY){
			//seconda figura in alto a sinsitra
			if (firstX - firstWidth < secondX + secondWidth && firstY - firstHeight < secondY + secondHeight)
				return true;
		}
		
		if (firstX < secondX && firstY < secondY){
			//seconda figura in basso a sinistra
			if (firstX - firstWidth < secondX + secondWidth && firstY + firstHeight > secondY - secondHeight)
				return true;
		}
		
		if (firstX == secondX && firstY == secondY){
			//seconda figura sovrapposta
				return true;
		}
		
		if (firstX == secondX && firstY > secondY){
			//seconda figura sopra alla prima
			if (firstY - firstHeight < secondY + secondHeight)
				return true;
		}
		
		if (firstX > secondX && firstY == secondY){
			//seconda figura a destra della prima
			if (firstX + firstWidth > secondX - secondWidth)
				return true;
		}
		
		if (firstX == secondX && firstY < secondY){
			//seconda figura sotto la prima
			if (firstY + firstHeight > secondY - secondHeight)
				return true;
		}
		
		if (firstX < secondX && firstY == secondY){
			//seconda figura a sinistra della prima
			if (firstX - firstWidth < secondX + secondWidth)
				return true;
		}
		return false;
	}
}
