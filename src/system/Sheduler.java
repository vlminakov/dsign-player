package system;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Calendar.Builder;
import java.util.Date;
import java.util.Vector;

import com.sun.org.apache.xpath.internal.FoundIndex;

import main.Resource;

public class Sheduler {

	private Vector<Resource>	resources;
	private boolean				cycle;
	private int					countResource;
	private long				timeToStart;
	private boolean				indexFound;

	public Sheduler() {
		resources = new Vector<Resource>();
		countResource = 0;
		indexFound = false;
	}

	@SuppressWarnings("unchecked")
	public Vector<Resource> getResources() {
		return (Vector<Resource>)this.resources.clone();
	}

	public void setResources(Vector<Resource> resources) {
		this.resources = resources;
	}

	//get resource. if vector.size() = 0 - return null
	public Resource getResource() {
//		if (countResource >= resources.size()) {
//			countResource = 0;
//		}
//		if (resources.size() == 0)
//			return null;
//		if (!isCycle())
//			return resources.remove(countResource);
//		return resources.elementAt(countResource++);
		Resource res = null;
		if (this.resources.size() > 0){
			if (!indexFound) {
				if (System.currentTimeMillis() >= this.resources.lastElement().getMediaStartTime() + this.resources.lastElement().getDuration()){
					res = this.resources.elementAt(0);
					Builder b = new Builder();
					Calendar c = b.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
							.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
							.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
							.setTimeOfDay(23, 59, 59).build();
					long lDate = c.getTimeInMillis();
					res.setCount(lDate - System.currentTimeMillis());
					this.resources.clear();
					return res;
				}
				if (System.currentTimeMillis() <= this.timeToStart){
					res = this.resources.elementAt(countResource);
					indexFound = true;
				} else {
					long curTime = System.currentTimeMillis();
					for (int i = 0; i < this.resources.size(); i++) {
						res = resources.elementAt(i);
						if (curTime > res.getMediaStartTime() && curTime < res.getMediaStartTime() + res.getDuration()){
							countResource = i;
							indexFound = true;
							break;
						}
					}
					long pause = res.getMediaStartTime() + res.getDuration() - System.currentTimeMillis();
					res = this.resources.elementAt(0);
					res.setCount(pause);
				}
			} else {
				countResource++;
				if (countResource >= this.resources.size()){
					res = this.resources.elementAt(0);
					Builder b = new Builder();
					Calendar c = b.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
							.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
							.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
							.setTimeOfDay(23, 59, 59).build();
					long lDate = c.getTimeInMillis();
					res.setCount(lDate - System.currentTimeMillis());
					this.resources.clear();
				} else {
					res = this.resources.elementAt(countResource);
				}
			}
		}
		
		return res;
	}

	public void addResource(Resource resource) {
		if (this.resources.size() == 0){
			resource.setMediaStartTime(timeToStart);
		} else {
			long tmpTime = timeToStart;
			for (Resource res : resources) {
				tmpTime += res.getDuration();
			}
			resource.setMediaStartTime(tmpTime);
		}
		
		resources.add(resource);
	}

	public void removeResource(Resource resource) {
		resources.remove(resource);
	}

	public boolean isCycle() {
		return cycle;
	}

	public void setCycle(boolean cicle) {
		this.cycle = cicle;
	}

	public long getTimeToStart() {
		return timeToStart;
	}

	public void setTimeToStart(long timeToStart) {
		this.timeToStart = timeToStart;
	}

}
