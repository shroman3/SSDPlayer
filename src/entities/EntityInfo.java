package entities;

import java.util.List;

public class EntityInfo {
	private List<EntityInfoEntry> entityInfoList = new java.util.ArrayList();

	public EntityInfo() {
		this.entityInfoList = new java.util.ArrayList();
	}

	public void add(String desc, String value, int order) {
		int i = 0;
		while (i < this.entityInfoList.size()) {
			if (((EntityInfoEntry) this.entityInfoList.get(i)).order > order) {
				break;
			}
			i++;
		}

		this.entityInfoList.add(i, new EntityInfoEntry(desc, value, order));
	}

	public List<EntityInfoEntry> getInfoList() {
		return this.entityInfoList;
	}
}
