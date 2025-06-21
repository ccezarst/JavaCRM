package TeamCore.DefaultComponents.Interfaces.Template;

import TeamCore.DefaultComponents.ComponentType;
import TeamCore.TeamCore;

public abstract class HardwareInterface extends Interface {
    private static ComponentType[] addX(ComponentType arr[], ComponentType x) {

        ComponentType newarr[] = new ComponentType[arr.length + 1];

        // insert the elements from
        // the old array into the new array
        // insert all elements till n
        // then insert x at n+1
        for (int i = 0; i < arr.length; i++)
            newarr[i] = arr[i];

        newarr[newarr.length - 1] = x;

        return newarr;
    }
    public HardwareInterface(String cName, Boolean active, TeamCore core, InterfaceType interfaceTye, ComponentType... types){
        super(cName, active, core, interfaceTye,addX(types, ComponentType.HARDARE_INTERFACE));
    }
}
