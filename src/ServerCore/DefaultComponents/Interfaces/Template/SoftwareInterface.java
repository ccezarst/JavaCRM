package ServerCore.DefaultComponents.Interfaces.Template;

import ServerCore.ServerCore;
import ServerCore.DefaultComponents.ComponentType;

public abstract class SoftwareInterface extends Interface {
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

    public SoftwareInterface(String cName, Boolean active, ServerCore core, InterfaceType interfaceTye, ComponentType... types){
        super(cName, active, core, interfaceTye, addX(types, ComponentType.SOFTWARE_INTERFACE));
    }
}
