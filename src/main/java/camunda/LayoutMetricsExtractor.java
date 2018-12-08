package camunda;

import java.util.ArrayList;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.di.Edge;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

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
		return this.getWaypointsMeasures(edges);
	}
	
	private class Segment{
		private Waypoint w1;
		private Waypoint w2;
		
		public Segment(Waypoint w1, Waypoint w2){
			this.w1 = w1;
			this.w2 = w2;
		}
		
		public Waypoint getFirstWaypoint(){
			return this.w1;
		}
		
		public Waypoint getSecondWaypoint(){
			return this.w2;
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
		
		return this.checkIntersections(segments);
	}
	
	private int checkIntersections(ArrayList<Segment> segments){
		int numberOfIntersections = 0;
		System.out.println(segments.size());
		for (Segment firstSegment: segments){
			//Segment firstSegment = segments.get(i);
			for (Segment secondSegment: segments){
				//Segment secondSegment = segments.get(j);
				if (!(firstSegment.equals(secondSegment))){ //TOFIX controllare che i segmenti non condividano lo stesso waypoint
					System.out.println("Intersezione :" + this.isIntersected(firstSegment.w1, firstSegment.w2, secondSegment.w1, secondSegment.w2));
					if(this.isIntersected(firstSegment.w1, firstSegment.w2, secondSegment.w1, secondSegment.w2) == true){
						numberOfIntersections ++;
					}
				}
					
			}
		}
		System.out.println(numberOfIntersections);
		return numberOfIntersections;
	}
	
	/**
	 * Check where or not the point w3 lies on the [w1,w2] segment
	 * @param w1
	 * @param w2
	 * @param w3
	 * @return true if w3 lies on the [w1,w2] segment
	 */
	private boolean pointOnSegment(Waypoint w1, Waypoint w2, Waypoint w3){
		if (w3.getX() <= Math.max(w1.getX(), w2.getX()) && w2.getX() >= Math.min(w1.getX(), w2.getX()) && 
			w3.getY() <= Math.max(w1.getY(), w2.getY()) && w2.getY() >= Math.min(w1.getY(),  w2.getY()))
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
	private int orientation(Waypoint w1, Waypoint w2, Waypoint w3){
		double val = (w3.getY() + w1.getY()) * (w2.getX() - w3.getX()) -
				  (w3.getX() - w1.getX()) * (w2.getY() - w3.getY());
		
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
		
//		System.out.println("Waypoint 1: " + w1.getX() + ", " + w1.getY());
//		System.out.println("Waypoint 2: " + w2.getX() + ", " + w2.getY());
//		System.out.println("Waypoint 3: " + w3.getX() + ", " + w3.getY());
//		System.out.println("Waypoint 4: " + w4.getX() + ", " + w4.getY());
//		System.out.println("Orientation 1: " + o1);
//		System.out.println("Orientation 2: " + o2);
//		System.out.println("Orientation 3: " + o3);
//		System.out.println("Orientation 4: " + o4);
		
		if (o1 != o2 && o3 != o4){
			return true;
		}
		if (o1 == 0 && this.pointOnSegment(w1, w2, w3)) 
			return true;
		
		if (o2 == 0 && this.pointOnSegment(w1, w2, w4)) 
			return true;
		
		if (o3 == 0 && this.pointOnSegment(w3, w4, w1)) 
			return true;
		
		if (o4 == 0 && this.pointOnSegment(w3, w4, w2)) 
			return true;
		
		return false;
	}
	//Controllo brutto
//	!((firstSegment.w1.getX() == secondSegment.w1.getX() || firstSegment.w1.getX() == secondSegment.w2.getX() || 
//			firstSegment.w2.getX() == secondSegment.w1.getX() || firstSegment.w2.getX() == secondSegment.w2.getX() )&&
//			(firstSegment.w1.getY() == secondSegment.w1.getY() || firstSegment.w1.getY() == secondSegment.w2.getY() || 
//			firstSegment.w2.getY() == secondSegment.w1.getY() || firstSegment.w2.getY() == secondSegment.w2.getY())))
//	
}
