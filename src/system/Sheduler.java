package system;

import java.util.Vector;

import main.Resource;

public class Sheduler {

	private Vector<Resource>	resources;
	private boolean				cycle;
	private int					countResource;
	private long				timeToStart;

	public Sheduler() {
		resources = new Vector<Resource>();
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
		if (countResource >= resources.size()) {
			countResource = 0;
		}
		if (resources.size() == 0)
			return null;
		if (!isCycle())
			return resources.remove(countResource);
		return resources.elementAt(countResource++);
	}

	public void addResource(Resource resource) {
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
