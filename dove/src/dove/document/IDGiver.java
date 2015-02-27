package dove.document;

import dove.api.ComponentApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IDGiver {
    public HashMap<Integer, ComponentApi> idMap        = new HashMap<>();
    public ArrayList<Integer>             availableIDs = new ArrayList<>();
    public int                            nextID       = 0;

    public IDGiver(DocumentContext doc) {
        if (doc.idGiver != null)
            throw new IllegalStateException("already instancised for this runtime");

        doc.idGiver = this;
    }

    public void giveID(ComponentApi component) {
        int id;

        if (availableIDs.isEmpty())
            id = nextID++;
        else
            id = availableIDs.remove(0);

        idMap.put(id, component);
    }

    public void releaseID(ComponentApi component) {
        idMap.remove(getIDForComponent(component));
    }

    public int getIDForComponent(ComponentApi component) {
        return idMap.entrySet().stream().
                filter((Map.Entry<Integer, ComponentApi> c) -> c.equals(component)).
                findAny().get().getKey();
    }
}